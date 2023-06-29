package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByBooker_Id(Long bookerId, Pageable pageable);

    boolean existsByBooker_Id(Long bookerId);

    Page<Booking> findByItemOwnerId(Long bookerId, Pageable pageable);

    Booking findFirstByItemIdAndStartBeforeAndStatus(Long itemId, LocalDateTime now, BookingStatus status, Sort sort);

    Booking findFirstByItemIdAndStartAfterAndStatus(Long itemId, LocalDateTime now, BookingStatus status, Sort sort);

    List<Booking> findByItemIdInAndStartBeforeAndStatus(List<Long> itemIds, LocalDateTime now, BookingStatus status, Sort sort);

    List<Booking> findByItemIdInAndStartAfterAndStatus(List<Long> itemIds, LocalDateTime now, BookingStatus status, Sort sort);

    Page<Booking> findByBookerIdAndEndBefore(Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime dat1, LocalDateTime dat2, Pageable pageable);

    Page<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndEndBefore(Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStartAfter(Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime dat1, LocalDateTime dat2, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    boolean existsByBookerIdAndEndBefore(Long bookerId, LocalDateTime now);

}
