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
    Optional<Receipt> findByHouseAndReceiptNameAndTypeService(@NotNull House house, @NotNull String receiptName, @NotNull TypeService typeService);
    @Query(value = "select r from Receipt r inner join House h on h.id = r.house.id inner join User u on u.id = h.user.id where u.id = ?1 order by r.id DESC")
    List<Receipt> getReceiptByUser(Long id);
    @Query(value = "select u.id from User u inner join House h on h.user.id = u.id inner join Receipt r on r.house.id = h.id where r.id = ?1")
    Long findUserByReceiptId(Long id);
    @Query(value = "select r from Receipt r inner join House h on h.id = r.house.id inner join User u on u.id = h.user.id where u.id = ?1 order by r.id DESC LIMIT 1")
    Optional<Receipt> getLastReceipt(Long id);
    @Query(value = "select r.id from Receipt r inner join House h on h.id = r.house.id inner join User u on u.id = h.user.id where u.id = ?1 and r.typeService.type = ?2 order by r.id DESC")
    List<Long> getIdByUser(Long idUSer, String typeReceipt);
    @Query(value = "select r from Receipt r where r.id = ?1 or r.id = ?2 order by r.id DESC")
    List<Receipt> getTwoReceiptsById(Long idReceipt1, Long idReceipt2);
    @Query(value = "select r from Receipt r inner join House h on h.id = r.house.id inner join User u on u.id = h.user.id where u.id = ?1 and h.name = ?2")
    Collection<Receipt> getAllReceiptsByHouse(Long idUser, String houseName);
    @Query(value = "select r from Receipt r where r.house = ?1 and r.typeService = ?2 and MONTH(r.date) = ?3 and YEAR(r.date) = ?4")
    List<Receipt> findByHouseAndTypeServiceAndMonthAndYear(House house, TypeService typeService, int month, int year);
    @Query(value = "select r from Receipt r inner join House h on r.house.id = h.id where h.id = ?1 and MONTH(r.date) = ?2")
    Collection<Receipt> listReceiptByHouseAndMonth(Long idHouse, int month);
    @Query(value = "select r.id from Receipt r where r.receiptName = ?1")
    Long findIdByName(String name);

}

