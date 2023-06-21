package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Controller
@Validated
@RequestMapping("/requests")
public class ItemRequestController {

    private final RequestClient requestClient;

    @Autowired
    public ItemRequestController(RequestClient requestClient) {
        this.requestClient = requestClient;
    }

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestBody @Valid ItemRequestDtoInput itemRequestDtoInput,
                                              @RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Add request with description={} by userId={}", itemRequestDtoInput.getDescription(), userId);
        return requestClient.saveRequest(itemRequestDtoInput, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable("requestId") Long id, @RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Get request with id={} by userId={}", id, userId);
        return  requestClient.getRequestById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Get requests with by userId={}", userId);
        return  requestClient.getRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequests(@RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(name = "size", defaultValue = "10") @PositiveOrZero Integer size,
                                               @RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Get all requests from={} with size={}, from userId={}", from, size, userId);
        return  requestClient.getAllRequests(from, size, userId);
    }
}
