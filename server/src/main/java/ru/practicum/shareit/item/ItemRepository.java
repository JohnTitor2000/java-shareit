package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerIdOrderById(Long ownerId);

    @Query("SELECT i FROM Item i WHERE LOWER(i.description) LIKE LOWER(concat('%', :description, '%')) AND i.available = true")
    List<Item> findByDescription(@Param("description") String description);

    List<Item> findByRequestId(Long requestId);

}
