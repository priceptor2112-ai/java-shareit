package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerId(Long bookerId, Sort sort);

    List<Booking> findByItemOwnerId(Long ownerId, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.bookerId = :bookerId AND b.start < :now AND b.end > :now")
    List<Booking> findCurrentByBookerId(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.itemOwnerId = :ownerId AND b.start < :now AND b.end > :now")
    List<Booking> findCurrentByItemOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.bookerId = :bookerId AND b.end < :now")
    List<Booking> findPastByBookerId(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.itemOwnerId = :ownerId AND b.end < :now")
    List<Booking> findPastByItemOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.bookerId = :bookerId AND b.start > :now")
    List<Booking> findFutureByBookerId(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.itemOwnerId = :ownerId AND b.start > :now")
    List<Booking> findFutureByItemOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, Sort sort);

    Optional<Booking> findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(Long itemId, LocalDateTime now, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime now, BookingStatus status);

    boolean existsByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime now);
}