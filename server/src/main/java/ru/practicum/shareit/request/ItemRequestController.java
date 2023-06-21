package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestOutput;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequest addRequest(@RequestBody ItemRequestDtoInput itemRequestDtoInput, @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemRequestService.saveRequest(itemRequestDtoInput, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestOutput getRequestById(@PathVariable("requestId") Long id, @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return  itemRequestService.getRequestById(id, userId);
    }

    @GetMapping
    public List<ItemRequestOutput> getRequests(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return  itemRequestService.getRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestOutput> getRequests(@RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                               @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
                                               @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return  itemRequestService.getAllRequests(from, size, userId);
    }
}
