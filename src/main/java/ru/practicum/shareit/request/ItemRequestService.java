package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exaption.BadRequestException;
import ru.practicum.shareit.exaption.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestOutput;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ItemRequestService {

    UserRepository userRepository;
    ItemRequestMapper itemRequestMapper;
    ItemRequestRepository itemRequestRepository;
    ItemRepository itemRepository;

    public ItemRequest saveRequest(ItemRequestDtoInput itemRequestDtoInput, Long userId) {
        if (itemRequestDtoInput.getDescription() == null) {
            throw new BadRequestException("Description cant be null");
        }
        return itemRequestRepository.save(itemRequestMapper.itemRequestDtoInputToItemRequest(itemRequestDtoInput,
                userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not Found"))));
    }

    public ItemRequestOutput getRequestById(Long id, Long userId) {
        checkUser(userId);
        List<Item> itemResponses = itemRepository.findByRequestId(id);
        ItemRequest itemRequest = itemRequestRepository.findById(id).orElseThrow(() -> new NotFoundException("Request not found"));
        return itemRequestMapper.itemRequestToItemRequestOutput(itemRequest, itemResponses);
    }

    public List<ItemRequestOutput> getRequests(Long userId) {
        checkUser(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequesterId(userId);
        List<ItemRequestOutput> requestOutputs = itemRequests.stream()
                .map(itemRequest -> itemRequestMapper.itemRequestToItemRequestOutput(itemRequest, itemRepository.findByRequestId(itemRequest.getId())))
                .collect(Collectors.toList());
        return requestOutputs;
    }

    public List<ItemRequestOutput> getAllRequests(int from, int size, Long userId) {
        Pageable pageable = PageRequest.of(from, size);
        Page<ItemRequest> itemRequestPage = itemRequestRepository.findAllByOrderByCreatedDesc(pageable);
        List<ItemRequest> itemRequests = itemRequestPage.getContent();

        List<ItemRequestOutput> requestOutputs = itemRequests.stream()
                .map(itemRequest -> itemRequestMapper.itemRequestToItemRequestOutput(
                        itemRequest, itemRepository.findByRequestId(itemRequest.getId())))
                .filter(o -> !o.getRequester().getId().equals(userId))
                .collect(Collectors.toList());
        return requestOutputs;
    }

    private void checkUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User Not Found");
        }
    }
}
