package proyecto.web.serviceguideBackend;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import proyecto.web.serviceguideBackend.auth.dto.LoginResponse;
import proyecto.web.serviceguideBackend.auth.interfaces.AuthInterface;
import proyecto.web.serviceguideBackend.auth.dto.CredentialsDto;
import proyecto.web.serviceguideBackend.auth.dto.SignUpDto;
import proyecto.web.serviceguideBackend.auth.interfaces.LoginInterface;
import proyecto.web.serviceguideBackend.city.City;
import proyecto.web.serviceguideBackend.config.UserAuthenticationProvider;
import proyecto.web.serviceguideBackend.dto.*;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.house.House;
import proyecto.web.serviceguideBackend.house.dto.HouseDto;
import proyecto.web.serviceguideBackend.house.interfaces.HouseInterface;
import proyecto.web.serviceguideBackend.house.interfaces.HouseRepository;
import proyecto.web.serviceguideBackend.receipt.Receipt;
import proyecto.web.serviceguideBackend.receipt.dto.ReceiptDto;
import proyecto.web.serviceguideBackend.receipt.interfaces.ReceiptInterface;
import proyecto.web.serviceguideBackend.receipt.interfaces.ReceiptRepository;
import proyecto.web.serviceguideBackend.receipt.typeService.TypeService;
import proyecto.web.serviceguideBackend.statistic.Statistic;
import proyecto.web.serviceguideBackend.statistic.dto.StatisticDto;
import proyecto.web.serviceguideBackend.statistic.dto.SumOfReceiptDto;
import proyecto.web.serviceguideBackend.statistic.interfaces.StatisticInterface;
import proyecto.web.serviceguideBackend.user.dto.UpdateResponse;
import proyecto.web.serviceguideBackend.user.dto.UpdateUserDto;
import proyecto.web.serviceguideBackend.user.User;
import proyecto.web.serviceguideBackend.user.dto.UserDto;
import proyecto.web.serviceguideBackend.user.dto.UserLoadDto;
import proyecto.web.serviceguideBackend.user.interfaces.UserRepository;
import proyecto.web.serviceguideBackend.user.interfaces.UserInterface;

import java.util.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RunWith(SpringRunner.class)
@SpringBootTest
class ServiceguideBackendApplicationTests {

    @Autowired
    private AuthInterface authInterface;

    @Autowired
    private LoginInterface loginInterface;

    @Autowired
    private HouseInterface houseInterface;

    @Autowired
    private ReceiptInterface receiptInterface;

    @Autowired
    private StatisticInterface statisticInterface;

    @Autowired
    private UserInterface userInterface;

    @Autowired
    private UserAuthenticationProvider userAuthenticationProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private HouseRepository houseRepository;

    @Order(1)
    @Test
    void contextLoads() {
    }

    @Order(2)
    @Test
    void testUserRegister() {
        SignUpDto signUpDto = new SignUpDto();
        signUpDto.setFirstName("Prueba");
        signUpDto.setLastName("Test");
        signUpDto.setEmail("prueba@gmail.com");
        signUpDto.setPassword("prueba123".toCharArray());

        UserDto registeredUser = authInterface.register(signUpDto);

        Assertions.assertNotNull(registeredUser.getId());
        Assertions.assertEquals(signUpDto.getEmail(), registeredUser.getEmail());
        Assertions.assertEquals(signUpDto.getFirstName(), registeredUser.getFirstName());
        Assertions.assertEquals(signUpDto.getLastName(), registeredUser.getLastName());
    }

    @Order(3)
    @Test
    void testAuthFindByEmail() {
        String email = "prueba@gmail.com";

        UserDto userDto = authInterface.findByEmail(email);

        Assertions.assertNotNull(userDto);
        Assertions.assertEquals(email, userDto.getEmail());
    }

    @Order(4)
    @Test
    void testUserLogin() {
        CredentialsDto credentialsDto = new CredentialsDto("prueba@gmail.com", "prueba123".toCharArray());

        LoginResponse loginResponse = loginInterface.login(credentialsDto);

        Assertions.assertNotNull(loginResponse);
    }

    @Order(5)
    @Test
    void testUserGetByEmail() {
        CredentialsDto credentialsDto = new CredentialsDto("prueba@gmail.com", "prueba123".toCharArray());
        LoginResponse loginResponse = loginInterface.login(credentialsDto);

        Optional<User> optionalUser = userInterface.getByEmail("prueba@gmail.com");

        Assertions.assertTrue(optionalUser.isPresent(), "El usuario no está presente");
        optionalUser.ifPresent(user -> {
            Assertions.assertEquals("Prueba", user.getFirstName());
            Assertions.assertEquals("Test", user.getLastName());
        });
    }

    @Order(6)
    @Test
    void testUserLoadById() {
        CredentialsDto credentialsDto = new CredentialsDto("prueba@gmail.com", "prueba123".toCharArray());
        LoginResponse loginResponse = loginInterface.login(credentialsDto);

        UserLoadDto userLoadDto = userInterface.loadById(loginResponse.getToken());

        Assertions.assertNotNull(userLoadDto);
        Assertions.assertEquals("prueba@gmail.com", userLoadDto.getEmail());
        Assertions.assertEquals("Prueba", userLoadDto.getFirstName());
        Assertions.assertEquals("Test", userLoadDto.getLastName());
    }

    @Order(7)
    @Test
    void testUserLoadUser() {
        CredentialsDto credentialsDto = new CredentialsDto("prueba@gmail.com", "prueba123".toCharArray());
        LoginResponse loginResponse = loginInterface.login(credentialsDto);
        Long idUser = userAuthenticationProvider.whoIsMyId(loginResponse.getToken());

        UserLoadDto userLoadDto = userInterface.loadUser(idUser);

        Assertions.assertNotNull(userLoadDto);
        Assertions.assertEquals("prueba@gmail.com", userLoadDto.getEmail());
        Assertions.assertEquals("Prueba", userLoadDto.getFirstName());
        Assertions.assertEquals("Test", userLoadDto.getLastName());
    }

    @Order(8)
    @Test
    void testUserUpdateUser() {
        CredentialsDto credentialsDto = new CredentialsDto("prueba@gmail.com", "prueba123".toCharArray());
        LoginResponse loginResponse = loginInterface.login(credentialsDto);
        Long idUser = userAuthenticationProvider.whoIsMyId(loginResponse.getToken());

        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setFirstName("Prueba Update");
        updateUserDto.setLastName("Test Update");
        updateUserDto.setEmail("pruebaUPDATE@gmail.com");
        updateUserDto.setPassword("prueba123".toCharArray());

        Optional<UpdateResponse> updatedUserDto = userInterface.updateUser(updateUserDto, idUser);
        Assertions.assertEquals("User updated", updatedUserDto.get().getMessage());

        User updatedUser = userRepository.findByEmail(updateUserDto.getEmail()).orElse(null);

        Assertions.assertEquals(updateUserDto.getFirstName(), updatedUser.getFirstName());
        Assertions.assertEquals(updateUserDto.getLastName(), updatedUser.getLastName());
    }

    @Order(9)
    @Test
    void testHouseNewHouse() {
        CredentialsDto credentialsDto = new CredentialsDto("pruebaUPDATE@gmail.com", "prueba123".toCharArray());
        LoginResponse loginResponse = loginInterface.login(credentialsDto);
        Long idUser = userAuthenticationProvider.whoIsMyId(loginResponse.getToken());

        Optional<User> optionalUser = userInterface.getByEmail("pruebaUPDATE@gmail.com");
        User user = optionalUser.orElseThrow(() -> new RuntimeException("User not found"));

        City city = new City();
        city.setId(1L);
        city.setCity("Medellín");

        HouseDto houseDto = new HouseDto();
        houseDto.setName("Prueba Test ._@");
        houseDto.setStratum(6);
        houseDto.setNeighborhood("La Castellana");
        houseDto.setAddress("Carrera 34 AA");
        houseDto.setContract("123456");
        houseDto.setCities(city);
        houseDto.setUser(user);

        HouseDto createdHouseDto = houseInterface.newHouse(houseDto, idUser);

        Assertions.assertNotNull(createdHouseDto);
        Assertions.assertEquals(houseDto.getName(), createdHouseDto.getName());
        Assertions.assertEquals(houseDto.getStratum(), createdHouseDto.getStratum());
        Assertions.assertEquals(houseDto.getNeighborhood(), createdHouseDto.getNeighborhood());
        Assertions.assertEquals(houseDto.getAddress(), createdHouseDto.getAddress());
        Assertions.assertEquals(houseDto.getContract(), createdHouseDto.getContract());
    }

    @Order(10)
    @Test
    void testHouseFindByUserAndName() {
        CredentialsDto credentialsDto = new CredentialsDto("pruebaUPDATE@gmail.com", "prueba123".toCharArray());
        LoginResponse loginResponse = loginInterface.login(credentialsDto);
        Long idUser = userAuthenticationProvider.whoIsMyId(loginResponse.getToken());

        String name = "Prueba Test ._@";

        Optional<House> optionalHouse = houseInterface.findByUserAndName(idUser, name);

        Assertions.assertNotNull(optionalHouse);
        Assertions.assertEquals(name, optionalHouse.get().getName());
    }

    @Order(11)
    @Test
    void testHouseFindAllByUserOrderById() {
        CredentialsDto credentialsDto = new CredentialsDto("pruebaUPDATE@gmail.com", "prueba123".toCharArray());
        LoginResponse loginResponse = loginInterface.login(credentialsDto);
        Long idUser = userAuthenticationProvider.whoIsMyId(loginResponse.getToken());

        String name = "Prueba Test ._@";

        Collection<House> houseCollection = houseInterface.findAllByUserOrderById(idUser);
        boolean found = false;

        for (House houseName : houseCollection) {
            if (houseName.getName().equals(name)) {
                found = true;
                break;
            }
        }

        Assertions.assertNotNull(houseCollection);
        Assertions.assertTrue(found, "El nombre '" + name + "' no se encuentra en la colección de nombres de casas.");
    }

    @Order(12)
    @Test
    void testHouseUpdateHouse() {
        CredentialsDto credentialsDto = new CredentialsDto("pruebaUPDATE@gmail.com", "prueba123".toCharArray());
        LoginResponse loginResponse = loginInterface.login(credentialsDto);
        Long idUser = userAuthenticationProvider.whoIsMyId(loginResponse.getToken());

        String houseName = "Prueba Test ._@";

        City city = new City();
        city.setId(1L);
        city.setCity("Medellín");

        Optional<House> houseOptional = houseInterface.findByUserAndName(idUser, houseName);
        Long id = houseOptional.get().getId();

        HouseDto updateHouse = new HouseDto();
        updateHouse.setName("Casa Prueba Update @._");
        updateHouse.setStratum(6);
        updateHouse.setNeighborhood("La Castellana");
        updateHouse.setContract("123456");
        updateHouse.setCities(city);

        Optional<Message> updateResult = houseInterface.updateHouse(updateHouse, id);

        Assertions.assertTrue(updateResult.isPresent(), "La actualización de la casa falló");
        Message message = updateResult.get();
        Assertions.assertEquals("House Updated successfully", message.getMessage(), "El mensaje de actualización de la casa es incorrecto");
        Assertions.assertEquals(HttpStatus.OK, message.getStatus(), "El estado de la respuesta de actualización de la casa es incorrecto");

    }

    @Order(13)
    @Test
    void testHouseGetHouseName() {
        CredentialsDto credentialsDto = new CredentialsDto("pruebaUPDATE@gmail.com", "prueba123".toCharArray());
        LoginResponse loginResponse = loginInterface.login(credentialsDto);
        Long idUser = userAuthenticationProvider.whoIsMyId(loginResponse.getToken());

        Collection<String> houseCollection = houseInterface.getHouseName(idUser);
        String name = "Casa Prueba Update @._";
        boolean found = false;

        for (String houseName : houseCollection) {
            if (houseName.equals(name)) {
                found = true;
                break;
            }
        }

        Assertions.assertNotNull(houseCollection);
        Assertions.assertTrue(found, "El nombre '" + name + "' no se encuentra en la colección de nombres de casas.");
    }

    @Order(14)
    @Test
    void testReceiptsNewReceipt() {
        CredentialsDto credentialsDto = new CredentialsDto("pruebaUPDATE@gmail.com", "prueba123".toCharArray());
        LoginResponse loginResponse = loginInterface.login(credentialsDto);
        Long idUser = userAuthenticationProvider.whoIsMyId(loginResponse.getToken());

        Calendar calendar = Calendar.getInstance();
        calendar.set(2023, Calendar.FEBRUARY, 10);
        Date date = calendar.getTime();

        TypeService typeService = new TypeService();
        typeService.setType("WATER");

        String houseName = "Casa Prueba Update @._";
        Optional<House> optionalHouse = houseInterface.findByUserAndName(idUser, houseName);
        House house = optionalHouse.orElseThrow(() -> new RuntimeException("House not found"));

        ReceiptDto receiptDto = new ReceiptDto();
        receiptDto.setReceiptName("Recibo Prueba Test @._");
        receiptDto.setPrice(20000D);
        receiptDto.setAmount(100F);
        receiptDto.setDate(date);
        receiptDto.setTypeService(typeService);
        receiptDto.setHouse(house);

        ReceiptDto createdReceiptDto = receiptInterface.newReceipt(receiptDto, idUser);

        Assertions.assertNotNull(createdReceiptDto);
        Assertions.assertEquals(receiptDto.getReceiptName(), createdReceiptDto.getReceiptName());
        Assertions.assertEquals(receiptDto.getAmount(), createdReceiptDto.getAmount());
        Assertions.assertEquals(receiptDto.getPrice(), createdReceiptDto.getPrice());
    }

    @Order(15)
    @Test
    void testReceiptsAllReceiptsByUserId() {
        CredentialsDto credentialsDto = new CredentialsDto("pruebaUPDATE@gmail.com", "prueba123".toCharArray());
        LoginResponse loginResponse = loginInterface.login(credentialsDto);
        Long idUser = userAuthenticationProvider.whoIsMyId(loginResponse.getToken());

        List<Receipt> receiptList = receiptInterface.allReceiptsByUserId(idUser);

        Assertions.assertNotNull(receiptList);
    }

    @Order(16)
    @Test
    void testReceiptsGetLastReceipt() {
        CredentialsDto credentialsDto = new CredentialsDto("pruebaUPDATE@gmail.com", "prueba123".toCharArray());
        LoginResponse loginResponse = loginInterface.login(credentialsDto);
        Long idUser = userAuthenticationProvider.whoIsMyId(loginResponse.getToken());

        String lastReceiptName = "Recibo Prueba Test @._";

        Optional<Receipt> optionalReceipt = receiptInterface.getLastReceipt(idUser);

        Assertions.assertNotNull(optionalReceipt);
        Assertions.assertEquals(lastReceiptName, optionalReceipt.get().getReceiptName());
    }


    @Order(17)
    @Test
    void testReceiptsGetTwoReceiptById() {
        CredentialsDto credentialsDto = new CredentialsDto("pruebaUPDATE@gmail.com", "prueba123".toCharArray());
        LoginResponse loginResponse = loginInterface.login(credentialsDto);
        Long idUser = userAuthenticationProvider.whoIsMyId(loginResponse.getToken());

        Long idReceipt = receiptRepository.findIdByName("Recibo Prueba Test @._");

        Long userID = receiptInterface.getTwoReceiptById(idReceipt);

        Assertions.assertNotNull(userID);
        Assertions.assertEquals(idUser, userID);
    }

    @Order(18)
    @Test
    void testReceiptUpdateReceipt() {
        CredentialsDto credentialsDto = new CredentialsDto("pruebaUPDATE@gmail.com", "prueba123".toCharArray());
        LoginResponse loginResponse = loginInterface.login(credentialsDto);
        Long idUser = userAuthenticationProvider.whoIsMyId(loginResponse.getToken());

        Calendar calendar = Calendar.getInstance();
        calendar.set(2023, Calendar.FEBRUARY, 10);
        Date date = calendar.getTime();

        TypeService typeService = new TypeService();
        typeService.setType("ENERGY");

        String houseName = "Casa Prueba Update @._";
        Optional<House> optionalHouse = houseInterface.findByUserAndName(idUser, houseName);
        House house = optionalHouse.orElseThrow(() -> new RuntimeException("House not found"));

        Long idReceipt = receiptRepository.findIdByName("Recibo Prueba Test @._");

        ReceiptDto receiptDto = new ReceiptDto();
        receiptDto.setReceiptName("Recibo Test Update ._@");
        receiptDto.setPrice(20000D);
        receiptDto.setAmount(100F);
        receiptDto.setDate(date);
        receiptDto.setTypeService(typeService);
        receiptDto.setHouse(house);

        Optional<Message> updatedResult = receiptInterface.updateReceipt(receiptDto, idReceipt);

        Assertions.assertTrue(updatedResult.isPresent(), "La actualización del recibo falló");
        Message message = updatedResult.get();
        Assertions.assertEquals("Receipt Updated successfully", message.getMessage(), "El mensaje de actualización del recibo es incorrecto");
        Assertions.assertEquals(HttpStatus.OK, message.getStatus(), "El estado de la respuesta de actualización de la casa es incorrecto");
    }

    @Order(19)
    @Test
    void testStatisticIndividualReceipt() {
        CredentialsDto credentialsDto = new CredentialsDto("pruebaUPDATE@gmail.com", "prueba123".toCharArray());
        LoginResponse loginResponse = loginInterface.login(credentialsDto);

        String typeReceipt = "ENERGY";
        String typeGraphic = "BAR";
        Long idReceipt = receiptRepository.findIdByName("Recibo Test Update ._@");

        Assertions.assertThrows(AppException.class, () -> {
            statisticInterface.individualReceipt(typeReceipt, idReceipt, typeGraphic);
        }, "Recibo creado pero no se puede generar la estadistica");

        AppException exception = Assertions.assertThrows(AppException.class, () -> {
            statisticInterface.individualReceipt(typeReceipt, idReceipt, typeGraphic);
        });
        Assertions.assertEquals(HttpStatus.OK, exception.getStatus());
    }

    @Order(20)
    @Test
    void testStatisticSumStatisticByType() {
        CredentialsDto credentialsDto = new CredentialsDto("pruebaUPDATE@gmail.com", "prueba123".toCharArray());
        LoginResponse loginResponse = loginInterface.login(credentialsDto);
        Long idUser = userAuthenticationProvider.whoIsMyId(loginResponse.getToken());

        String houseName = "Casa Prueba Update @._";

        double[] sumatoria = statisticInterface.sumStatisticByType(idUser, houseName);

        Assertions.assertNotNull(sumatoria);
        Assertions.assertEquals(4, sumatoria.length);

        double waterSum = sumatoria[0];
        double energySum = sumatoria[1];
        double gasSum = sumatoria[2];
        double sewerageSum = sumatoria[3];

        Assertions.assertEquals(0D, waterSum);
        Assertions.assertEquals(100D, energySum);
        Assertions.assertEquals(0D, gasSum);
        Assertions.assertEquals(0D, sewerageSum);
    }

    @Order(21)
    @Test
    void testStatisticSumOfReceiptDto() {
        CredentialsDto credentialsDto = new CredentialsDto("pruebaUPDATE@gmail.com", "prueba123".toCharArray());
        LoginResponse loginResponse = loginInterface.login(credentialsDto);

        String houseName = "Casa Prueba Update @._";
        Long idHouse = houseRepository.findIdByName(houseName);

        SumOfReceiptDto sumOfReceiptDto = statisticInterface.sumOfReceiptDto(idHouse);

        Assertions.assertNotNull(sumOfReceiptDto);
        Assertions.assertEquals(0f, sumOfReceiptDto.getLastSumMonth());
        Assertions.assertEquals(20000f, sumOfReceiptDto.getSumMonth());
        Assertions.assertEquals(0f, sumOfReceiptDto.getPercentage());
        Assertions.assertEquals(0f, sumOfReceiptDto.getDifference());

    }

    @Order(22)
    @Test
    void testReceiptDeleteReceipt() {
        CredentialsDto credentialsDto = new CredentialsDto("pruebaUPDATE@gmail.com", "prueba123".toCharArray());
        LoginResponse loginResponse = loginInterface.login(credentialsDto);

        Long idReceipt = receiptRepository.findIdByName("Recibo Test Update ._@");

        Message deletedReceipt = receiptInterface.deleteReceipt(idReceipt);

        Assertions.assertNotNull(deletedReceipt);
        Assertions.assertEquals("Received deleted successfully", deletedReceipt.getMessage());
    }

    @Order(23)
    @Test
    void testHouseDeleteHouse() {
        CredentialsDto credentialsDto = new CredentialsDto("pruebaUPDATE@gmail.com", "prueba123".toCharArray());
        LoginResponse loginResponse = loginInterface.login(credentialsDto);

        Long idHouse = houseRepository.findIdByName("Casa Prueba Update @._");

        Message deleteMessage = houseInterface.deleteHouse(idHouse);


        Assertions.assertNotNull(deleteMessage);
        Assertions.assertEquals("Delete success", deleteMessage.getMessage());
    }

    @Order(24)
    @Test
    void testUserDelete() {
        CredentialsDto credentialsDto = new CredentialsDto("pruebaUPDATE@gmail.com", "prueba123".toCharArray());
        LoginResponse loginResponse = loginInterface.login(credentialsDto);
        Long idUser = userAuthenticationProvider.whoIsMyId(loginResponse.getToken());

        Message deleteMessage = userInterface.delete(idUser);

        Assertions.assertNotNull(deleteMessage);
        Assertions.assertEquals("Delete success", deleteMessage.getMessage());
    }
}

