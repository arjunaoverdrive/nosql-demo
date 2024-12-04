package eu.senla.service.Impl;

import eu.senla.domain.User;
import eu.senla.model.UserModel;
import eu.senla.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    @Override
    public void notifyUsers(Collection<UserModel> users) {

        Set<String> emails = users.stream()
                .map(UserModel::getEmail)
                        .collect(Collectors.toSet());
        //send email logic
        log.info("Notification sent to users {}", emails);
    }
}