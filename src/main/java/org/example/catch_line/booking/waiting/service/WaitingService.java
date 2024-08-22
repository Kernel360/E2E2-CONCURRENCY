package org.example.catch_line.booking.waiting.service;

import org.example.catch_line.booking.waiting.model.dto.WaitingRequest;
import org.example.catch_line.booking.waiting.model.dto.WaitingResponse;
import org.example.catch_line.booking.waiting.model.entity.WaitingEntity;
import org.example.catch_line.booking.waiting.model.mapper.WaitingResponseMapper;
import org.example.catch_line.booking.waiting.repository.WaitingRepository;
import org.example.catch_line.common.constant.Status;
import org.example.catch_line.history.validation.HistoryValidator;
import org.example.catch_line.user.member.model.entity.MemberEntity;
import org.example.catch_line.user.member.model.provider.validation.MemberValidator;
import org.example.catch_line.dining.restaurant.model.entity.RestaurantEntity;
import org.example.catch_line.dining.restaurant.validation.RestaurantValidator;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WaitingService {

	private final WaitingRepository waitingRepository;
	private final WaitingResponseMapper waitingResponseMapper;
	private final HistoryValidator historyValidator;
	private final MemberValidator memberValidator;
	private final RestaurantValidator restaurantValidator;

	public WaitingResponse addWaiting(Long restaurantId, WaitingRequest waitingRequest, Long memberId) {

		MemberEntity member = memberValidator.checkIfMemberPresent(memberId);
		RestaurantEntity restaurant = restaurantValidator.checkIfRestaurantPresent(restaurantId);

		WaitingEntity waiting = WaitingEntity.builder()
			.memberCount(waitingRequest.getMemberCount())
			.status(Status.SCHEDULED)
			.waitingType(waitingRequest.getWaitingType())
			.member(member)
			.restaurant(restaurant)
			.build();
		WaitingEntity savedEntity = waitingRepository.save(waiting);

		return waitingResponseMapper.convertToResponse(savedEntity);
	}

	public void cancelWaiting(Long waitingId) {
		WaitingEntity entity = historyValidator.checkIfWaitingPresent(waitingId);

		entity.changeWaitingStatus(Status.CANCELED);
		waitingRepository.save(entity);
	}

	public boolean isExistingWaiting(Long memberId, Status status) {
		return waitingRepository.existsByMemberMemberIdAndStatus(memberId, status);
	}

}
