package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDtoGateway;
import ru.practicum.shareit.item.dto.ItemDtoGateway;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsGateWay;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(ItemDtoGateway item, Long userId) {
        return post("/", userId, item);
    }

    public ResponseEntity<Object> getItemsByUser(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> deleteItem(Long id, Long userId) {
        return delete("/" + id, userId);
    }

    public ResponseEntity<Object> updateItem(Long id, ItemDtoWithBookingsGateWay itemDTOWithBookingsGateWay, Long userId) {
        return patch("/" + id, userId, itemDTOWithBookingsGateWay);
    }

    public ResponseEntity<Object> search(String text, Long userId) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("/search?text={text}", userId, parameters);
    }

    public ResponseEntity<Object> getItemById(Long id, Long userId) {
        return get("/" + id, userId);
    }

    public ResponseEntity addComment(Long itemId, Long userId, CommentDtoGateway commentDtoGateway) {
        return post("/" + itemId + "/comment", userId, commentDtoGateway);
    }
}
