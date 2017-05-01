package server;

import common.ProcessInfo;

import java.util.Comparator;
import java.util.Optional;
import java.util.TreeSet;
import java.util.UUID;

/**
 * Created by 1 on 01.05.2017.
 */
public class RMIServiceImpl implements RMIService {

    private UUID uuid = UUID.randomUUID();

    private TreeSet<ProcessInfo> processes = new TreeSet<>(Comparator.comparingInt(ProcessInfo::getLoad));

    @Override
    public ProcessInfo getMinProcess() {
        return processes.first();
    }

    @Override
    public void acceptProcess(ProcessInfo processInfo) {
        System.out.println("Принят новый процесс " + processInfo.getUuid() + ". Сервер id: " + uuid);
        processes.add(processInfo);
        processInfo.getProcess().run();
    }

    @Override
    public void removeProcess(UUID uuid) {
        System.out.println("Извлечь процесс " + uuid + ". Сервер id: " + this.uuid);
        Optional<ProcessInfo> processInfoOptional = processes.stream().
                filter(processInfo -> processInfo.getUuid().equals(uuid)).findAny();
        if (processInfoOptional.isPresent()) {
            processInfoOptional.get().getProcess().interrupt();
            processes.remove(processInfoOptional.get());
        }
    }
}
