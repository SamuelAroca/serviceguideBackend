package proyecto.web.serviceguideBackend;

import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import proyecto.web.serviceguideBackend.auth.interfaces.AuthInterface;
import proyecto.web.serviceguideBackend.auth.dto.CredentialsDto;
import proyecto.web.serviceguideBackend.auth.dto.SignUpDto;
import proyecto.web.serviceguideBackend.city.City;
import proyecto.web.serviceguideBackend.config.UserAuthenticationProvider;
import proyecto.web.serviceguideBackend.dto.*;
import proyecto.web.serviceguideBackend.house.House;
import proyecto.web.serviceguideBackend.house.dto.HouseDto;
import proyecto.web.serviceguideBackend.house.interfaces.HouseInterface;
import proyecto.web.serviceguideBackend.receipt.Receipt;
import proyecto.web.serviceguideBackend.receipt.dto.ReceiptDto;
import proyecto.web.serviceguideBackend.receipt.interfaces.ReceiptInterface;
import proyecto.web.serviceguideBackend.receipt.typeService.TypeService;
import proyecto.web.serviceguideBackend.statistic.Statistic;
import proyecto.web.serviceguideBackend.statistic.interfaces.StatisticInterface;
import proyecto.web.serviceguideBackend.user.dto.UpdateUserDto;
import proyecto.web.serviceguideBackend.user.User;
import proyecto.web.serviceguideBackend.user.dto.UserDto;
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
    void testUserLogin() {

        CredentialsDto credentialsDto = new CredentialsDto("prueba@gmail.com", "prueba123".toCharArray());

        UserDto userDto = authInterface.login(credentialsDto);

        Assertions.assertNotNull(userDto);
        Assertions.assertEquals("prueba@gmail.com", userDto.getEmail());
    }

    @Order(4)
    @Test
    void testAuthFindByEmail() {
        String email = "prueba@gmail.com";

        UserDto userDto = authInterface.findByEmail(email);

        Assertions.assertNotNull(userDto);
        Assertions.assertEquals(email, userDto.getEmail());
    }

    @Order(5)
    @Test
    void testUserUpdateUser() {
        CredentialsDto credentialsDto = new CredentialsDto("prueba@gmail.com", "prueba123".toCharArray());
        UserDto userDto = authInterface.login(credentialsDto);
        userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));
        String token = userDto.getToken();

        SignUpDto updateUserDto = new SignUpDto();
        updateUserDto.setFirstName("Prueba Update");
        updateUserDto.setLastName("Test Update");
        updateUserDto.setEmail("pruebaUPDATE@gmail.com");
        updateUserDto.setPassword("prueba123".toCharArray());

        Optional<UpdateUserDto> updatedUserDto = userInterface.updateUser(updateUserDto, token);
        Assertions.assertEquals("User updated", updatedUserDto.get().getMessage());

        User updatedUser = userRepository.findByEmail(updateUserDto.getEmail()).orElse(null);

        Assertions.assertEquals(updateUserDto.getFirstName(), updatedUser.getFirstName());
        Assertions.assertEquals(updateUserDto.getLastName(), updatedUser.getLastName());
    }

    @Order(6)
    @Test
    void testHouseNewHouse() {
        CredentialsDto credentialsDto = new CredentialsDto("pruebaUPDATE@gmail.com", "prueba123".toCharArray());
        UserDto userDto = authInterface.login(credentialsDto);
        userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

        String token = userDto.getToken();

        Optional<User> optionalUser = userInterface.findById(token);
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

        HouseDto createdHouseDto = houseInterface.newHouse(houseDto, token);

        Assertions.assertNotNull(createdHouseDto);
        Assertions.assertEquals(houseDto.getName(), createdHouseDto.getName());
        Assertions.assertEquals(houseDto.getStratum(), createdHouseDto.getStratum());
        Assertions.assertEquals(houseDto.getNeighborhood(), createdHouseDto.getNeighborhood());
        Assertions.assertEquals(houseDto.getAddress(), createdHouseDto.getAddress());
        Assertions.assertEquals(houseDto.getContract(), createdHouseDto.getContract());
    }

    @Order(7)
    @Test
    void testHouseFindByUserAndName() {
        CredentialsDto credentialsDto = new CredentialsDto("pruebaUPDATE@gmail.com", "prueba123".toCharArray());
        UserDto userDto = authInterface.login(credentialsDto);
        userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

        String token = userDto.getToken();
        String name = "Prueba Test ._@";

        Optional<House> optionalHouse = houseInterface.findByUserAndName(token, name);

        Assertions.assertNotNull(optionalHouse);
        Assertions.assertEquals(name, optionalHouse.get().getName());
    }

    @Order(8)
    @Test
    void testHouseUpdateHouse() {
        CredentialsDto credentialsDto = new CredentialsDto("pruebaUPDATE@gmail.com", "prueba123".toCharArray());
        UserDto userDto = authInterface.login(credentialsDto);
        userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

        String token = userDto.getToken();
        String houseName = "Prueba Test ._@";

        City city = new City();
        city.setId(1L);
        city.setCity("Medellín");

        Optional<House> houseOptional = houseInterface.findByUserAndName(token, houseName);
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

    @Order(9)
    @Test
    void testHouseGetHouseName() {
        CredentialsDto credentialsDto = new CredentialsDto("pruebaUPDATE@gmail.com", "prueba123".toCharArray());
        UserDto userDto = authInterface.login(credentialsDto);
        userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

        String token = userDto.getToken();

        Collection<String> houseCollection = houseInterface.getHouseName(token);
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

    @Order(10)
    @Test
    void testReceiptsNewReceipt() {
        CredentialsDto credentialsDto = new CredentialsDto("pruebaUPDATE@gmail.com", "prueba123".toCharArray());
        UserDto userDto = authInterface.login(credentialsDto);
        userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

        String token = userDto.getToken();

        Calendar calendar = Calendar.getInstance();
        calendar.set(2023, Calendar.FEBRUARY, 10);
        Date date = calendar.getTime();

        TypeService typeService = new TypeService();
        typeService.setType("WATER");

        String houseName = "Casa Prueba Update @._";
        Optional<House> optionalHouse = houseInterface.findByUserAndName(token, houseName);
        House house = optionalHouse.orElseThrow(() -> new RuntimeException("House not found"));

        ReceiptDto receiptDto = new ReceiptDto();
        receiptDto.setReceiptName("Recibo Prueba Test @._");
        receiptDto.setPrice(20000D);
        receiptDto.setAmount(100D);
        receiptDto.setDate(date);
        receiptDto.setTypeService(typeService);
        receiptDto.setHouse(house);

        ReceiptDto createdReceiptDto = receiptInterface.newReceipt(receiptDto, token);

        Assertions.assertNotNull(createdReceiptDto);
        Assertions.assertEquals(receiptDto.getReceiptName(), createdReceiptDto.getReceiptName());
        Assertions.assertEquals(receiptDto.getAmount(), createdReceiptDto.getAmount());
        Assertions.assertEquals(receiptDto.getPrice(), createdReceiptDto.getPrice());
    }

    @Order(11)
    @Test
    void testReceiptsAllReceiptsByUserId() {
        CredentialsDto credentialsDto = new CredentialsDto("pruebaUPDATE@gmail.com", "prueba123".toCharArray());
        UserDto userDto = authInterface.login(credentialsDto);
        userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

        String token = userDto.getToken();

        List<Receipt> receiptList = receiptInterface.allReceiptsByUserId(token);

        Assertions.assertNotNull(receiptList);
    }

    @Order(12)
    @Test
    void testReceiptsGetLastReceipt() {
        CredentialsDto credentialsDto = new CredentialsDto("pruebaUPDATE@gmail.com", "prueba123".toCharArray());
        UserDto userDto = authInterface.login(credentialsDto);
        userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

        String token = userDto.getToken();
        String lastReceiptName = "Recibo Prueba Test @._";

        Optional<Receipt> optionalReceipt = receiptInterface.getLastReceipt(token);

        Assertions.assertNotNull(optionalReceipt);
        Assertions.assertEquals(lastReceiptName, optionalReceipt.get().getReceiptName());
    }

    @Order(13)
    @Test
    void testReceiptUpdateReceipt() {
        CredentialsDto credentialsDto = new CredentialsDto("pruebaUPDATE@gmail.com", "prueba123".toCharArray());
        UserDto userDto = authInterface.login(credentialsDto);
        userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

        String token = userDto.getToken();

        Calendar calendar = Calendar.getInstance();
        calendar.set(2023, Calendar.FEBRUARY, 10);
        Date date = calendar.getTime();

        TypeService typeService = new TypeService();
        typeService.setType("ENERGY");

        String houseName = "Casa Prueba Update @._";
        Optional<House> optionalHouse = houseInterface.findByUserAndName(token, houseName);
        House house = optionalHouse.orElseThrow(() -> new RuntimeException("House not found"));

        Long idReceipt = receiptInterface.findIdByName("Recibo Prueba Test @._");

        ReceiptDto receiptDto = new ReceiptDto();
        receiptDto.setReceiptName("Recibo Test Update ._@");
        receiptDto.setPrice(20000D);
        receiptDto.setAmount(100D);
        receiptDto.setDate(date);
        receiptDto.setTypeService(typeService);
        receiptDto.setHouse(house);

        Optional<Message> updatedResult = receiptInterface.updateReceipt(receiptDto, idReceipt);

        Assertions.assertTrue(updatedResult.isPresent(), "La actualización del recibo falló");
        Message message = updatedResult.get();
        Assertions.assertEquals("Receipt Updated successfully", message.getMessage(), "El mensaje de actualización del recibo es incorrecto");
        Assertions.assertEquals(HttpStatus.OK, message.getStatus(), "El estado de la respuesta de actualización de la casa es incorrecto");
    }

    @Order(14)
    @Test
    void testReceiptGetAllReceiptsByType() {
        CredentialsDto credentialsDto = new CredentialsDto("pruebaUPDATE@gmail.com", "prueba123".toCharArray());
        UserDto userDto = authInterface.login(credentialsDto);
        userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

        String token = userDto.getToken();
        String type = "ENERGY";
        String receiptName = "Recibo Test Update ._@";
        Double receiptPrice = 20000D;

        Collection<Receipt> receiptCollection = receiptInterface.getAllReceiptsByType(token, type);

        boolean found = false;

        for (Receipt receipts : receiptCollection) {
            Double price = receipts.getPrice();
            System.out.println(price);
            if (receipts != null && receipts.getReceiptName().equals(receiptName) && receipts.getPrice().equals(receiptPrice)) {
                found = true;
                break;
            }
        }

        Assertions.assertNotNull(receiptCollection);
        Assertions.assertTrue(found, "El '" + receiptName + "' con precio '" + receiptPrice + "' no se encuentra en la colección de recibos");
    }

    @Order(15)
    @Test
    void testStatisticGetStatisticByReceipt() {
        String receiptName = "Recibo Test Update ._@";
        Long id = receiptInterface.findIdByName(receiptName);
        List<Statistic> statistics = statisticInterface.getStatisticByReceipt(id);

        Assertions.assertNotNull(statistics);
    }

    @Order(16)
    @Test
    void testReceiptDeleteReceipt() {
        CredentialsDto credentialsDto = new CredentialsDto("pruebaUPDATE@gmail.com", "prueba123".toCharArray());
        UserDto userDto = authInterface.login(credentialsDto);
        userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

        Long idReceipt = receiptInterface.findIdByName("Recibo Test Update ._@");

        Message deletedReceipt = receiptInterface.deleteReceipt(idReceipt);

        Assertions.assertNotNull(deletedReceipt);
        Assertions.assertEquals("Received deleted successfully", deletedReceipt.getMessage());
    }

    @Order(17)
    @Test
    void testHouseDeleteHouse() {
        CredentialsDto credentialsDto = new CredentialsDto("pruebaUPDATE@gmail.com", "prueba123".toCharArray());
        UserDto userDto = authInterface.login(credentialsDto);
        userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

        Long idHouse = houseInterface.findIdByName("Casa Prueba Update @._");

        Message deleteMessage = houseInterface.deleteHouse(idHouse);


        Assertions.assertNotNull(deleteMessage);
        Assertions.assertEquals("Delete success", deleteMessage.getMessage());
    }

    @Order(18)
    @Test
    void testUserDelete() {
        CredentialsDto credentialsDto = new CredentialsDto("pruebaUPDATE@gmail.com", "prueba123".toCharArray());
        UserDto userDto = authInterface.login(credentialsDto);
        userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));
        String token = userDto.getToken();

        Message deleteMessage = userInterface.delete(token);

        Assertions.assertNotNull(deleteMessage);
        Assertions.assertEquals("Delete success", deleteMessage.getMessage());
    }
}

