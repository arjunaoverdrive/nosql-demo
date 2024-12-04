package eu.senla.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.senla.model.TaskModel;
import eu.senla.model.UserModel;
import eu.senla.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class Subscriber implements MessageListener {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public void onMessage(Message message, byte[] pattern) {

        log.info("Received message {}", message);
        TaskModel task = objectMapper.readValue(message.getBody(), TaskModel.class);
        List<UserModel> users = new ArrayList<>(task.getObservers());
        users.add(task.getAssignee());
        users.add(task.getAuthor());
        notificationService.notifyUsers(users);
    }
}
