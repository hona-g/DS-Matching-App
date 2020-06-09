package User;

import kr.ac.konkuk.ccslab.cm.event.CMUserEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;

// 서비스에서는 프로세스 로직 예) 이미 존재하는 이메일인지 확인

public class UserService {
    private CMInfo cmInfo;
    private UserRepository userRepository;

    public UserService(CMInfo cmInfo) {
        this.cmInfo = cmInfo;
        this.userRepository = new UserRepository();
    }

    private boolean isExistedEmail(String email) {
        return userRepository.findByEmail(email, cmInfo) != null;
    }

    public User createUser(User user) {
        if(isExistedEmail(user.getEmail()))
            return null;

        int ret = userRepository.saveUser(user, cmInfo);
        if(ret == -1) return null;
        return user;
    }

    public Profile findProfile(CMUserEvent ue) {

        Long id = Long.valueOf(ue.getEventField(CMInfo.CM_LONG, "userId"));
        Profile profile = userRepository.getProfileByUserId(id, cmInfo);
        if(profile == null) return null; // TODO : Exception 처리

        return profile;
    }

    public User login(CMUserEvent ue) {

        String email = ue.getEventField(CMInfo.CM_STR, "email");
        String password = ue.getEventField(CMInfo.CM_STR, "password");

        User user = userRepository.findByEmail(email, cmInfo);

        if(user == null) return null; // TODO : Exception 처리

        if(!password.equals(user.getPassword())) return null; // TODO : Exception 처리

        return user;
    }


}
