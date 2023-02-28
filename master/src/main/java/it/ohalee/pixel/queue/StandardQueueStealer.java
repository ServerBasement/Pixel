package it.ohalee.pixel.queue;

import it.ohalee.pixel.api.QueueStealer;
import it.ohalee.pixel.match.PixelType;
import it.ohalee.pixel.match.SharedMatch;
import it.ohalee.pixel.player.PixelParticipator;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;

import java.util.Optional;

@RequiredArgsConstructor
public class StandardQueueStealer
        <E extends Enum<E> & PixelType, T extends SharedMatch<E>, P extends PixelParticipator, Q extends StandardQueue<E, T, P>>
        extends QueueStealer<E, T, P, Q> {

    protected final static int MAX_COUNT_NULL = 30;
    protected final RLock taskLock;
    protected final Q queue;
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

            Optional<P> playerOptional = queue.stealPlayer();
            if (playerOptional.isEmpty()) {
                countNull++;
                return;
            }
            P participator = playerOptional.get();

            T match = queue.seekMatch(1);
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
