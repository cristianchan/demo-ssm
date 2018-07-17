package com.example.demossm;

import java.util.List;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Transactional
public class StateMachineController {

    public final static String MACHINE_ID_1 = "datajpapersist1";
    public final static String MACHINE_ID_2 = "datajpapersist2";
    private final static String[] MACHINES = new String[]{MACHINE_ID_1, MACHINE_ID_2};

    private final StateMachineLogListener listener = new StateMachineLogListener();
    private StateMachine<String, String> currentStateMachine;

    @Autowired
    private StateMachineService<String, String> stateMachineService;

    @Autowired
    private StateMachinePersist<String, String, String> stateMachinePersist;

    @RequestMapping("/")
    public String home() {
        return "redirect:/state";
    }

    @RequestMapping("/state")
    public String feedAndGetStates(
            @RequestParam(value = "events", required = false) List<String> events,
            @RequestParam(value = "machine", required = false, defaultValue = MACHINE_ID_1) String machine,
            Model model) throws Exception {
        StateMachine<String, String> stateMachine = getStateMachine(machine);
        if (events != null) {
            for (String event : events) {
                stateMachine.sendEvent(event);
            }
        }
        StateMachineContext<String, String> stateMachineContext = stateMachinePersist.read(machine);
        model.addAttribute("allMachines", MACHINES);
        model.addAttribute("machine", machine);
        model.addAttribute("allEvents", getEvents());
        model.addAttribute("messages", createMessages(listener.getMessages()));
        model.addAttribute("context", stateMachineContext != null ? stateMachineContext.toString() : "");
        return "states";
    }

    private synchronized StateMachine<String, String> getStateMachine(String machineId) throws Exception {
        listener.resetMessages();
        if (currentStateMachine == null) {
            currentStateMachine = stateMachineService.acquireStateMachine(machineId);
            currentStateMachine.addStateListener(listener);
            currentStateMachine.start();
        } else if (!ObjectUtils.nullSafeEquals(currentStateMachine.getId(), machineId)) {
            stateMachineService.releaseStateMachine(currentStateMachine.getId());
            currentStateMachine.stop();
            currentStateMachine = stateMachineService.acquireStateMachine(machineId);
            currentStateMachine.addStateListener(listener);
            currentStateMachine.start();
        }
        return currentStateMachine;
    }

    private String[] getEvents() {
        String[] array = {"Evento2"};
        return array;
    }

    private String createMessages(List<String> messages) {
        StringBuilder buf = new StringBuilder();
        for (String message : messages) {
            buf.append(message);
            buf.append("\n");
        }
        return buf.toString();
    }
}
