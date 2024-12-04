package eu.senla.service.Impl;

import eu.senla.domain.Task;
import eu.senla.mapper.TaskMapper;
import eu.senla.model.TaskModel;
import eu.senla.service.PublishingService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublishingServiceImpl implements PublishingService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;
    private final TaskMapper taskMapper;

    @Override
    @SneakyThrows
    public void publish(Task task) {
        TaskModel taskModel = taskMapper.toModel(task);
        redisTemplate.convertAndSend(topic.getTopic(), taskModel);
        log.info("Published object: {}", taskModel);
    }
}