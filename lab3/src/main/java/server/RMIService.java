package server;

import common.ProcessInfo;

import java.util.UUID;

/**
 * Created by 1 on 01.05.2017.
 */
public interface RMIService {

    ProcessInfo getMinProcess();
    void acceptProcess(ProcessInfo processInfo);
    void removeProcess(UUID uuid);
}
