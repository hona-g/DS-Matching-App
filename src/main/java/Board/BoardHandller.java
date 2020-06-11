package Board;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Common.Result;
import Config.TokenProvider;
import Team.Team;
import kr.ac.konkuk.ccslab.cm.event.CMUserEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

public class BoardHandller {
    private CMServerStub cmServerStub;
    private BoardService boardService;
    private ObjectMapper objectMapper;

    public BoardHandller(CMServerStub cmServerStub) {
        this.cmServerStub = cmServerStub;
        this.boardService = new BoardService(cmServerStub.getCMInfo());
        this.objectMapper = new ObjectMapper();
    }

    /*
    	토큰 validation 확인 후 오류 시 에러 처리
	*/
	public TokenProvider.TokenResult getUserInfo(CMUserEvent ue) {
	    String token = ue.getEventField(CMInfo.CM_STR, "token");
	    if(token == null){
	        ue.setEventField(CMInfo.CM_INT, "success", "0");
	        ue.setEventField(CMInfo.CM_STR, "msg", "NOT AUTHORIZED");
	        cmServerStub.send(ue, ue.getSender());
	        return null;
	    }
	    return TokenProvider.validateToken(token);
	}
	
	public void handleError(Result result, CMUserEvent ue) {
	    ue.setEventField(CMInfo.CM_INT, "success", "0");
	    ue.setEventField(CMInfo.CM_STR, "msg", result.getMsg());
	    cmServerStub.send(ue, ue.getSender());
	}
	
	public void getBoards(CMUserEvent ue) {
		ue.setStringID("GET-BOARDS-REPLY");
        TokenProvider.TokenResult validResult = getUserInfo(ue);
        if(validResult == null) return;

        Long teamId = Long.valueOf(ue.getEventField(CMInfo.CM_LONG, "team_id"));
        if(teamId == null) {
            handleError(new Result("입력값을 확인하세요", false), ue);
            return;
        }
        
        Result result = new Result();
        List<Board> boards = boardService.getBoards(teamId, result);

        /*
            result값이 false이면 그에 대한 에러 메시지 처리
         */
        if(!result.isSuccess()) {
            handleError(result, ue);
            return;
        }

        try {
            String ret = objectMapper.writeValueAsString(boards);
            ue.setEventField(CMInfo.CM_INT, "success", "1");
            ue.setEventField(CMInfo.CM_STR, "msg", validResult.getSuccess());
            ue.setEventField(CMInfo.CM_STR, "board", ret);
            cmServerStub.send(ue, ue.getSender());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
