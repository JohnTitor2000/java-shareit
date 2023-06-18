package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/requests")
public class ItemRequestController {

    RequestClient requestClient;

    @Autowired
    public ItemRequestController(RequestClient requestClient) {
        this.requestClient = requestClient;
    }

    @PostMapping
    public ResponseEntity<Object> addRequestе(@RequestBody @Valid ItemRequestDtoInput itemRequestDtoInput, @RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        return requestClient.saveRequest(itemRequestDtoInput, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable("requestId") Long id, @RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        return  requestClient.getRequestById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        return  requestClient.getRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequests(@RequestParam(name = "from", required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(name = "size", required = false, defaultValue = "10") @PositiveOrZero Integer size,
                                               @RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        return  requestClient.getAllRequests(from, size, userId);
    }
}
