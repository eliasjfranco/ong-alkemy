package com.alkemy.ong.util;


// ELIMINAR ESTA CLASE PARA LA ETAPA DE PRODUCCIÓN

import com.alkemy.ong.controller.AuthController;
import com.alkemy.ong.dto.request.UsersRequestDto;
import com.alkemy.ong.model.Role;
import com.alkemy.ong.repository.RolRepository;
import com.alkemy.ong.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//Comentar despues de compilar por primera vez
/*
@Component
@AllArgsConstructor
public class UsersSeeder implements CommandLineRunner {

    private final UserRepository usersRepository;
    private final RolRepository rolRepository;
    private final AuthController controller;

    @Override
    public void run(String... args) throws Exception {
        usersRepository.createUserRole();
        usersRepository.createAdminRole();
        String line;
        List<String> lines = new ArrayList<>();
        ClassLoader classLoader = getClass().getClassLoader();
        try {
            InputStream inputStream = classLoader.getResourceAsStream("Seeder/Seed.csv");
            assert inputStream != null;
            InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(streamReader);
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }}catch (Exception e){
            throw new Exception("Hubo un error cargando los datos");
        }

        for(String l : lines){
            String[] usersData = l.split(",");
            try{
                Set<Role> roles = new HashSet<>();
                roles.add(rolRepository.findById(1L).get());
                UsersRequestDto currentUser = new UsersRequestDto();
                currentUser.setFirstName(usersData[0]);
                currentUser.setLastName(usersData[1]);
                currentUser.setEmail(usersData[2]);
                currentUser.setPassword(usersData[3]);
                controller.createUser(currentUser);

            } catch (Exception e){
                throw new Exception("Es probable que este usuario ya exista");
            }
        }
        for(int i=1; i<11; i++){
            try {
                usersRepository.setAdminRole((long) i);
            }catch (Exception e){
                throw new Exception("Es probable que este usuario ya sea Admin");
            }
        }
    }
}

*/