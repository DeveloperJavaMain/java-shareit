package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_Id(Long bookerId, Sort sort);

    boolean existsByBooker_Id(Long bookerId);

    List<Booking> findByItemOwnerId(Long bookerId, Sort sort);

    Booking findFirstByItemIdAndEndBefore(Long itemId, LocalDateTime now, Sort sort);

    Booking findFirstByItemIdAndStartAfter(Long itemId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndEndBefore(Long bookerId, LocalDateTime now);

}
