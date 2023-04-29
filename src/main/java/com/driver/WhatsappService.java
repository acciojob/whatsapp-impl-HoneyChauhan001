package com.driver;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WhatsappService {

    WhatsappRepository whatsappRepository = new WhatsappRepository();
    public String createUser(String name, String mobile) throws Exception {
        Optional<User> userOpt = whatsappRepository.getUserByMobile(mobile);
        if(userOpt.isPresent()){
            throw new Exception("User Already Exists");
        }
        else {
            User user = new User(name,mobile);
            whatsappRepository.createUser(mobile,user);
            return "SUCCESS";
        }
    }

    public Group createGroup(List<User> users) {
        int numberOfParticipants = users.size();
        Group group;
        if(numberOfParticipants == 2){
            group = new Group(users.get(1).getName(),numberOfParticipants);
        }
        else {
            int groupCount = whatsappRepository.getCustomGroupCount() + 1;
            whatsappRepository.setCustomGroupCount(groupCount);
            String groupName = "Group " + groupCount;
            group = new Group(groupName,numberOfParticipants);
        }
        whatsappRepository.createGroup(group,users);
        return group;

    }

    public int createMessage(String content) {
        int messageId = whatsappRepository.getMessageId() + 1;
        whatsappRepository.setMessageId(messageId);
        Message message = new Message(messageId,content);
        whatsappRepository.createMessage(message);
        return messageId;

    }

    public int sendMessage(Message message, User sender, Group group) throws Exception {
        Optional<Group> groupOpt = whatsappRepository.getGroup(group);
        if(groupOpt.isEmpty()){
            throw new Exception("Group does not exist");
        }
        else {
            Optional<User> userOpt = whatsappRepository.getUserInGroup(group,sender);
            if(userOpt.isEmpty()){
                throw new Exception("You are not allowed to send message");
            }
            else {
                return whatsappRepository.sendMessage(message,sender,group);
            }
        }
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception {
        Optional<Group> groupOpt = whatsappRepository.getGroup(group);
        if(groupOpt.isEmpty()){
            throw new Exception("Group does not exist");
        }
        else {
            Optional<User> adminOpt = whatsappRepository.getAdmin(group);
            if(!(adminOpt.get()).equals(approver)){
                throw new Exception("Approver does not have rights");
            }
            else {
                Optional<User> userOpt = whatsappRepository.getUserInGroup(group,user);
                if(userOpt.isEmpty()){
                    throw new Exception("User is not a participant");
                }
                else {
                    whatsappRepository.changeAdmin(group,user);
                    return "SUCCESS";
                }
            }
        }
    }

}
