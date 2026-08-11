package com.ecommerce.project.controller;

import com.ecommerce.project.model.AppRole;
import com.ecommerce.project.model.Role;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repositories.RoleRepository;
import com.ecommerce.project.repositories.UserRepository;
import com.ecommerce.project.security.jwt.JwtUtils;
import com.ecommerce.project.security.request.LoginRequest;
import com.ecommerce.project.security.request.SignupRequest;
import com.ecommerce.project.security.response.MessageResponse;
import com.ecommerce.project.security.response.UserInfoResponse;
import com.ecommerce.project.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    RoleRepository roleRepository;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {


        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad Credentials");
            map.put("status", false);

            return new ResponseEntity<Object>(map, HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
            UserInfoResponse loginResponse = new UserInfoResponse(userDetails.getId(), userDetails.getUsername(),roles, jwtCookie.toString());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(loginResponse) ;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {


        if (userRepository.existsByUserName(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error : The username is already in use.."));
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error : The email is already in use.."));
        }

        User user = new User(signupRequest.getUsername(),
                signupRequest.getEmail(),
                encoder.encode(signupRequest.getPassword()));

        Set<String> strRoles = signupRequest.getRoles();

        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByAppRole(AppRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error : Role is not found"));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role admin = roleRepository.findByAppRole(AppRole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error :  Role is not found.."));
                        roles.add(admin);
                        break;
                    case "user":
                        Role seller = roleRepository.findByAppRole(AppRole.ROLE_SELLER)
                                .orElseThrow(() -> new RuntimeException("Error :  Role is not found.."));
                        roles.add(seller);
                        break;
                    default:
                        Role userRole = roleRepository.findByAppRole(AppRole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error :  Role is not found.."));
                        roles.add(userRole);
                }
            });

            user.setRoles(roles);
            userRepository.save(user);

        }


        return ResponseEntity.ok(new MessageResponse("user registered successfully !!"));

    }
    @GetMapping("/username")
    public String currentUsername(Authentication authentication) {
        if(authentication != null) {
            return authentication.getName();

        }
        else{
            return "Username is null";
        }

    }

    @GetMapping("/user")
    public ResponseEntity<?> getUseName(Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        UserInfoResponse loginResponse = new UserInfoResponse(userDetails.getId(),userDetails.getUsername(), roles);

        return ResponseEntity.ok().body(loginResponse) ;

    }

    @PostMapping("/signout")
    public ResponseEntity<?> signoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new MessageResponse("Success fully logged out !! ")) ;

    }
}

