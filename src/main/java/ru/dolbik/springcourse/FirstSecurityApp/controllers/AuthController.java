package ru.dolbik.springcourse.FirstSecurityApp.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.dolbik.springcourse.FirstSecurityApp.dto.AuthenticationDTO;
import ru.dolbik.springcourse.FirstSecurityApp.dto.PersonDTO;
import ru.dolbik.springcourse.FirstSecurityApp.models.Person;
import ru.dolbik.springcourse.FirstSecurityApp.security.JWTUtil;
import ru.dolbik.springcourse.FirstSecurityApp.services.RegistrationService;
import ru.dolbik.springcourse.FirstSecurityApp.util.AuthErrorResponse;
import ru.dolbik.springcourse.FirstSecurityApp.util.AuthException;
import ru.dolbik.springcourse.FirstSecurityApp.util.PersonValidator;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Map;

import static ru.dolbik.springcourse.FirstSecurityApp.util.ErrorsUtil.returnErrorsClient;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final PersonValidator personValidator;
    private final RegistrationService registrationService;
    private final JWTUtil jwtUtil;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    @Autowired
    public AuthController(PersonValidator personValidator, RegistrationService registrationService, JWTUtil jwtUtil, ModelMapper modelMapper, AuthenticationManager authenticationManager) {
        this.personValidator = personValidator;
        this.registrationService = registrationService;
        this.jwtUtil = jwtUtil;
        this.modelMapper = modelMapper;
        this.authenticationManager = authenticationManager;
    }
    @PostMapping("/login")
    public Map<String, String> performLogin(@RequestBody AuthenticationDTO authenticationDTO){
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(authenticationDTO.getUsername(),
                        authenticationDTO.getPassword());

        try{
            authenticationManager.authenticate(authToken);
        }catch(BadCredentialsException exc){
            return Map.of("message: ", "Incorrect credentials");
        }
        String token = jwtUtil.generateToken(authenticationDTO.getUsername());
        return Map.of("jwt-token", token);
    }

    @PostMapping("/registration")
    public Map<String, String> performRegistration(@RequestBody @Valid PersonDTO personDTO,
                                                   BindingResult bindingResult){
        Person person = convertToPerson(personDTO);
        personValidator.validate(person, bindingResult);
        if (bindingResult.hasErrors()){
            returnErrorsClient(bindingResult);
        }
        registrationService.register(person);
        String token = jwtUtil.generateToken(person.getUsername());
        return Map.of("jwt-token", token);

    }
    public Person convertToPerson(PersonDTO personDTO){
        return this.modelMapper.map(personDTO, Person.class);
    }
    @ExceptionHandler
    private ResponseEntity<AuthErrorResponse> handleException(@NotNull final AuthException e){
        AuthErrorResponse response = new AuthErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
