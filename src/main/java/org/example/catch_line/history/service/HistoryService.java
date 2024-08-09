package org.example.catch_line.history.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.example.catch_line.common.constant.Status;
import org.example.catch_line.history.model.dto.HistoryResponse;
import org.example.catch_line.booking.reservation.model.entity.ReservationEntity;
import org.example.catch_line.booking.reservation.repository.ReservationRepository;
import org.example.catch_line.booking.waiting.model.entity.WaitingEntity;
import org.example.catch_line.booking.waiting.repository.WaitingRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoryService {

	private final WaitingRepository waitingRepository;
	private final ReservationRepository reservationRepository;

	public List<HistoryResponse> getAllHistory(Long memberId, Status status) {
		List<HistoryResponse> historyResponseList = new ArrayList<>();

		// 오늘 날짜의 시작과 끝을 계산
		LocalDateTime startOfDay = getStartOfDay();
		LocalDateTime endOfDay = getEndOfDay();

		// 오늘 등록된 상태가 SCHEDULED인 웨이팅 리스트 가져오기
		List<WaitingEntity> scheduledWaitingEntities = getWaitingEntitiesScheduledForToday(memberId, status, startOfDay, endOfDay);
		List<ReservationEntity> allReservation = getReservationEntities(memberId, status);

		// 각 웨이팅 엔티티를 HistoryResponse로 변환
		scheduledWaitingEntities.forEach(waiting ->
			historyResponseList.add(toHistoryResponse(waiting, startOfDay, endOfDay))
		);

		// 각 예약 엔티티를 HistoryResponse로 변환
		allReservation.forEach(reservation ->
			historyResponseList.add(reservationToHistoryResponse(reservation))
		);

		// 생성일 기준으로 내림차순 정렬
		sortHistoryResponsesByCreatedAt(historyResponseList);

		return historyResponseList;
	}

	private LocalDateTime getStartOfDay() {
		return LocalDate.now().atStartOfDay();
	}

	private LocalDateTime getEndOfDay() {
		return LocalDate.now().atTime(LocalTime.MAX);
	}

	// 상태가 예정이고 날짜가 오늘인 웨이팅 리스트
	private List<WaitingEntity> getWaitingEntitiesScheduledForToday(Long memberId, Status status, LocalDateTime startOfDay, LocalDateTime endOfDay) {
		return waitingRepository.findByMemberMemberIdAndStatusAndCreatedAtBetween(memberId, status, startOfDay, endOfDay);
	}

	// 예약 리스트 가져오기
	private List<ReservationEntity> getReservationEntities(Long memberId, Status status) {
		return reservationRepository.findByMemberMemberIdAndStatus(memberId, status);
	}

	private HistoryResponse toHistoryResponse(WaitingEntity waiting, LocalDateTime startOfDay, LocalDateTime endOfDay) {
		int waitingRegistrationId = calculateWaitingRegistrationId(waiting, startOfDay, endOfDay);
		int myWaitingPosition = calculateMyWaitingPosition(waiting, startOfDay, endOfDay);

		return HistoryResponse.builder()
			.restaurantId(waiting.getRestaurant().getRestaurantId())
			.waitingId(waiting.getWaitingId())
			.memberCount(waiting.getMemberCount())
			.restaurantName(waiting.getRestaurant().getName())
			.status(waiting.getStatus())
			.waitingType(waiting.getWaitingType())
			.serviceType(waiting.getRestaurant().getServiceType())
			.createdAt(waiting.getCreatedAt())
			.modifiedAt(waiting.getModifiedAt())
			.waitingRegistrationId(waitingRegistrationId)
			.myWaitingPosition(myWaitingPosition)
			.build();
	}

	private int calculateWaitingRegistrationId(WaitingEntity waiting, LocalDateTime startOfDay, LocalDateTime endOfDay) {
		long count = waitingRepository.countByRestaurantAndCreatedAtBetweenAndCreatedAtBefore(
			waiting.getRestaurant(), startOfDay, endOfDay, waiting.getCreatedAt());
		return (int) count + 1;
	}

	private int calculateMyWaitingPosition(WaitingEntity waiting, LocalDateTime startOfDay, LocalDateTime endOfDay) {
		long count = waitingRepository.countByRestaurantAndStatusAndCreatedAtBefore(
			waiting.getRestaurant(), Status.SCHEDULED, waiting.getCreatedAt());
		return (int) count + 1;
	}

	private void sortHistoryResponsesByCreatedAt(List<HistoryResponse> historyResponseList) {
		Collections.sort(historyResponseList, Comparator.comparing(HistoryResponse::getCreatedAt).reversed());
	}

	private HistoryResponse reservationToHistoryResponse(ReservationEntity entity) {
		return HistoryResponse.builder()
			.restaurantId(entity.getRestaurant().getRestaurantId())
			.reservationId(entity.getReservationId())
			.memberCount(entity.getMemberCount())
			.restaurantName(entity.getRestaurant().getName())
			.status(entity.getStatus())
			.reservationDate(entity.getReservationDate())
			.serviceType(entity.getRestaurant().getServiceType())
			.createdAt(entity.getCreatedAt())
			.modifiedAt(entity.getModifiedAt())
			.waitingRegistrationId(1) // 항상 1로 설정
			.myWaitingPosition(1) // 항상 1로 설정
			.build();
	}

	// 예약 상세 정보 조회
	public HistoryResponse findReservationDetailById(List<HistoryResponse> historyList, String reservationId) {
		return historyList.stream()
			.filter(h -> h.getReservationId() != null && reservationId.equals(h.getReservationId().toString()))  // null 체크 추가
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("오류: 해당 예약 정보를 찾을 수 없습니다."));
	}

	// 웨이팅 상세 정보 조회
	public HistoryResponse findWaitingDetailById(List<HistoryResponse> historyList, String waitingId) {
		return historyList.stream()
			.filter(h -> h.getWaitingId() != null && waitingId.equals(h.getWaitingId().toString()))  // null 체크 추가
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("오류: 해당 대기 정보를 찾을 수 없습니다."));
	}

}


