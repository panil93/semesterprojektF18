package dk.sdu.mmmi.cbse.common.data.entityparts;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;

/**
 *
 * @author Group 7
 */
public class CoinPart implements EntityPart {

    private int coins;

    public CoinPart(int coins) {
        this.coins = coins;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }
    
    @Override
    public void process(GameData gameData, Entity entity) {
		}
}
