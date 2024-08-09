package org.example.catch_line.booking.reservation.service;

import java.util.List;
import java.util.stream.Collectors;

import org.example.catch_line.booking.reservation.model.dto.ReservationRequest;
import org.example.catch_line.booking.reservation.model.dto.ReservationResponse;
import org.example.catch_line.booking.reservation.model.mapper.ReservationResponseMapper;
import org.example.catch_line.booking.reservation.repository.ReservationRepository;
import org.example.catch_line.common.constant.Status;
import org.example.catch_line.member.model.entity.MemberEntity;
import org.example.catch_line.member.repository.MemberRepository;
import org.example.catch_line.exception.service.ServiceIdException;
import org.example.catch_line.booking.reservation.model.entity.ReservationEntity;
import org.example.catch_line.restaurant.model.entity.RestaurantEntity;
import org.example.catch_line.restaurant.model.entity.constant.ServiceType;
import org.example.catch_line.restaurant.repository.RestaurantRepository;
import org.example.catch_line.exception.service.ServiceTypeException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {

	private final MemberRepository memberRepository;
	private final RestaurantRepository restaurantRepository;

	private final ReservationRepository reservationRepository;
	private final ReservationResponseMapper reservationResponseMapper;

	public ReservationResponse addReserve(Long restaurantId, ReservationRequest reservationRequest, Long memberId) {




		MemberEntity member = memberRepository.findById(memberId)
			.orElseThrow(() -> new ServiceIdException());

		RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
			.orElseThrow(() -> new ServiceIdException());

		if (restaurant.getServiceType() != ServiceType.RESERVATION) {
			throw new ServiceTypeException();
		}


		ReservationEntity reservation = ReservationEntity.builder()
			.member(member)
			.restaurant(restaurant)
			.memberCount(reservationRequest.getMemberCount())
			.status(Status.SCHEDULED)
			.reservationDate(reservationRequest.getReservationDate())
			.build();
		ReservationEntity savedEntity = reservationRepository.save(reservation);

		return reservationResponseMapper.convertToResponse(savedEntity);
	}

	public List<ReservationResponse> getAllReservation(Long memberId) {
		List<ReservationEntity> reservationEntities = reservationRepository.findByMemberMemberId(memberId);

		return reservationEntities.stream()
			.map(reservationResponseMapper::convertToResponse)
			.collect(Collectors.toList());
	}

	public ReservationResponse getReservationById(Long id) {
		ReservationEntity reservationEntity = reservationRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("정확한 아이디가 아닙니다: " + id));
		return reservationResponseMapper.convertToResponse(reservationEntity);
	}

	public void cancelReservation(Long id) {
		ReservationEntity reservationEntity = reservationRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("예약 아이디가 다릅니다: " + id));
		reservationEntity.changeReservationStatus(Status.CANCELED);
		reservationRepository.save(reservationEntity);
	}

	public void editReservation(Long id, ReservationRequest reservationRequest) {
		ReservationEntity reservationEntity = reservationRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("예약 아이디가 다릅니다: " + id));

		reservationEntity.updateReservation(reservationRequest);
		reservationRepository.save(reservationEntity);
	}

}