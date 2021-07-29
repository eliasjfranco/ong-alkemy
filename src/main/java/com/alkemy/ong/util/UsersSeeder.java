package com.alkemy.ong.util;


// ELIMINAR ESTA CLASE PARA LA ETAPA DE PRODUCCIÓN
/*
@Component
@AllArgsConstructor
public class UsersSeeder implements CommandLineRunner {

    private final UsersRepository usersRepository;
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
                UsersCreationDto currentUser = new UsersCreationDto();
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
