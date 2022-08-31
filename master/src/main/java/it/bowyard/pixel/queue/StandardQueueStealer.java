package it.bowyard.pixel.queue;

import it.bowyard.pixel.api.QueueStealer;
import it.bowyard.pixel.match.PixelType;
import it.bowyard.pixel.match.SharedMatch;
import it.bowyard.pixel.player.PixelParticipator;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;

import java.util.Optional;

@RequiredArgsConstructor
public class StandardQueueStealer
        <E extends Enum<E> & PixelType, T extends SharedMatch<E>, P extends PixelParticipator, Q extends StandardQueue<E, T, P>>
        extends QueueStealer<E, T, P, Q>
{

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
