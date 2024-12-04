package eu.senla.service;

import eu.senla.model.UserModel;

import java.util.Collection;

public interface NotificationService {

    void notifyUsers(Collection<UserModel> users);
}