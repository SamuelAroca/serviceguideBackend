package proyecto.web.serviceguideBackend.repositories;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import proyecto.web.serviceguideBackend.entities.House;
import proyecto.web.serviceguideBackend.entities.Receipt;
import proyecto.web.serviceguideBackend.entities.TypeService;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    Collection<Receipt> findByHouse(@NotNull House house);
    Collection<Receipt> findByTypeServiceAndHouse(@NotNull TypeService typeService, @NotNull House house);
    Optional<Receipt> findByHouseAndReceiptNameAndTypeService(@NotNull House house, @NotNull String receiptName, @NotNull TypeService typeService);

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

    @Query(value = "select r from Receipt r inner join House h on h.id = r.house.id inner join User u on u.id = h.user.id where u.id = ?1 and r.typeService.type = ?2 and YEAR(r.date) = ?3")
    Collection<Receipt> getAllReceiptByTypeAndYear(Long idUser, String type, int year);

    @Query(value = "select r from Receipt r inner join House h on h.id = r.house.id inner join User u on u.id = h.user.id where u.id = ?1 and r.typeService.type = ?2 and extract(quarter from r.date) = ?3 and YEAR(r.date) = ?4")
    Collection<Receipt> getReceiptsByQuarter(Long idUser, String type, int quarter, int receiptYear);

}

