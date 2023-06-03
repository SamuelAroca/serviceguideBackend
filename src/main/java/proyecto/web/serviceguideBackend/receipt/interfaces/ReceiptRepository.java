package proyecto.web.serviceguideBackend.receipt.interfaces;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import proyecto.web.serviceguideBackend.house.House;
import proyecto.web.serviceguideBackend.receipt.Receipt;
import proyecto.web.serviceguideBackend.receipt.typeService.TypeService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    Collection<Receipt> findByHouse(@NotNull House house);
    Collection<Receipt> findByTypeServiceAndHouse(@NotNull TypeService typeService, @NotNull House house);
    Optional<Receipt> findByHouseAndReceiptNameAndTypeService(@NotNull House house, @NotNull String receiptName, @NotNull TypeService typeService);

    @Query(value = "select r from Receipt r inner join House h on h.id = r.house.id where h.name = ?1")
    Collection<Receipt> findReceiptByHouse(@NotNull String houseName);

    @Query(value = "select r from Receipt r inner join House h on h.id = r.house.id inner join User u on u.id = h.user.id where u.id = ?1 order by r.id DESC")
    List<Receipt> getReceiptByUser(Long id);

    @Query(value = "select u.id from User u inner join House h on h.user.id = u.id inner join Receipt r on r.house.id = h.id where r.id = ?1")
    Long findUserByReceiptId(Long id);

    @Query(value = "select r from Receipt r inner join House h on h.id = r.house.id inner join User u on u.id = h.user.id where u.id = ?1 order by r.id DESC LIMIT 1")
    Optional<Receipt> getLastReceipt(Long id);

    @Query(value = "select r.id from Receipt r where r.receiptName = ?1")
    Long findIdByName(String name);

    @Query(value = "select r from Receipt r inner join House h on h.id = r.house.id inner join User u on u.id = h.user.id where u.id = ?1 and r.typeService.type = ?2")
    Collection<Receipt> getAllReceiptsByType(Long idUser, String type);

    @Query(value = "select r.id from Receipt r inner join House h on h.id = r.house.id inner join User u on u.id = h.user.id where u.id = ?1 and r.typeService.type = ?2 order by r.id DESC")
    List<Long> getIdByUser(Long idUSer, String typeReceipt);

    @Query(value = "select r from Receipt r where r.id = ?1 or r.id = ?2 order by r.id DESC")
    List<Receipt> getTwoReceiptsById(Long idReceipt1, Long idReceipt2);

    @Query(value = "select r from Receipt r inner join House h on h.id = r.house.id inner join User u on u.id = h.user.id where u.id = ?1 and r.typeService.type = ?2 and r.houseName = ?3")
    Collection<Receipt> getAllReceiptsByTypeAndHouse(Long idUser, String type, String house);

    @Query(value = "select r from Receipt r inner join House h on h.id = r.house.id inner join User u on u.id = h.user.id where u.id = ?1 and r.typeService.type = ?2 and year(r.date) = ?3")
    Collection<Receipt> getAllReceiptByTypeAndYear(Long idUser, String type, int year);

    @Query(value = "select r from Receipt r inner join House h on h.id = r.house.id inner join User u on u.id = h.user.id where u.id = ?1 and r.typeService.type = ?2 and extract(quarter from r.date) = ?3 and year(r.date) = ?4")
    Collection<Receipt> getReceiptsByQuarter(Long idUser, String type, int quarter, int receiptYear);

    @Query(value = "select r from Receipt r inner join House h on h.id = r.house.id inner join User u on u.id = h.user.id where u.id = ?1 and r.typeService.type = ?2 and extract(month from r.date) in ?3 and year(r.date) = ?4")
    Collection<Receipt> getReceiptsBySemester(Long idUser, String type, List<Integer> months, int receiptYear);

    @Query(value = "SELECT r FROM Receipt r INNER JOIN House h ON h.id = r.house.id INNER JOIN User u ON u.id = h.user.id WHERE u.id = ?1 AND r.typeService.type = ?2 AND EXTRACT(MONTH FROM r.date) BETWEEN ?3 AND ?4 AND EXTRACT(YEAR FROM r.date) = ?5")
    Collection<Receipt> getReceiptsByMonth(Long idUser, String type, int startMonth, int endMonth, int receiptYear);

    @Query(value = "select r from Receipt r inner join House h on h.id = r.house.id inner join User u on u.id = h.user.id where u.id = ?1 and h.name = ?2")
    Collection<Receipt> getAllReceiptsByHouse(Long idUser, String houseName);

    @Query(value = "select r from Receipt r where r.house = ?1 and r.typeService = ?2 and MONTH(r.date) = ?3 and YEAR(r.date) = ?4")
    List<Receipt> findByHouseAndTypeServiceAndMonthAndYear(House house, TypeService typeService, int month, int year);

}

