package org.example.catch_line.waiting.controller;

import org.example.catch_line.common.SessionUtils;
import org.example.catch_line.waiting.model.dto.WaitingRequest;
import org.example.catch_line.waiting.model.dto.WaitingResponse;
import org.example.catch_line.waiting.model.entity.WaitingType;
import org.example.catch_line.waiting.service.WaitingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WaitingController {

	private final WaitingService waitingService;

	@GetMapping("/restaurants/{restaurantId}/waiting")
	public String addWaitingForm(
		@PathVariable("restaurantId") Long restaurantId
	) {

		return "waiting/waiting";
	}

	@PostMapping("/restaurants/{restaurantId}/waiting")
	public String addWaiting(
		@PathVariable Long restaurantId,
		@RequestParam Integer memberCount,
		@RequestParam String waitingType,
		Model model,
		HttpSession session,
		RedirectAttributes redirectAttributes
	) {
		try {
			SessionUtils.getMemberId(session);

			WaitingType type = "DINE_IN".equals(waitingType) ? WaitingType.DINE_IN : WaitingType.TAKE_OUT;

			WaitingRequest waitingRequest = WaitingRequest.builder()
				.memberCount(memberCount)
				.waitingType(type)
				.build();

			WaitingResponse waitingResponse = waitingService.addWaiting(restaurantId, waitingRequest, session);

			model.addAttribute("waitingResponse", waitingResponse);

			return "redirect:/history";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Waiting failed: " + e.getMessage());
			return "redirect:/restaurants/" + restaurantId + "/waiting";
		}

	}

}
