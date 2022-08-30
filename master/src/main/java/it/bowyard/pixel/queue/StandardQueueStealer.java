package it.bowyard.pixel.queue;

import it.bowyard.pixel.match.SharedMatch;
import it.bowyard.pixel.player.PixelParticipator;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;
import org.redisson.api.RLock;

import java.util.Optional;

@RequiredArgsConstructor
public class StandardQueueStealer extends BukkitRunnable {

    protected final static int MAX_COUNT_NULL = 30;
    protected final RLock taskLock;
    protected final StandardQueue<?, ?, PixelParticipator> queue;
    protected int countNull = 0;

    @Override
    public void run() {

        if (!taskLock.tryLock()) return;

        try {
            if (queue.getRancher().getRunningServers() == 0)
                return;
            if (countNull == MAX_COUNT_NULL) {
                queue.idle();
                this.cancel();
                return;
            }

            Optional<PixelParticipator> playerOptional = queue.stealPlayer();
            if (playerOptional.isEmpty()) {
                countNull++;
                return;
            }
            PixelParticipator participator = playerOptional.get();

            SharedMatch<?> match = queue.seekMatch(1);
            if (match == null) {
                queue.addPlayer(participator, true);
                return;
            }

            match.join(participator.getPlayer());
        } catch (Exception exp) {
            exp.printStackTrace();
        } finally {
            taskLock.unlock();
        }

    }

}
