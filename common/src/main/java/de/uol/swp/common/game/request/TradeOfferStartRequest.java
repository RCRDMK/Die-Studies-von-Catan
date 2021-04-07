package de.uol.swp.common.game.request;
import de.uol.swp.common.user.UserDTO;

public class TradeOfferStartRequest extends AbstractGameRequest{

    private String item;
    private int itemCount;

    public TradeOfferStartRequest(UserDTO user, String gameName, String item, int itemCount){
        setUser(user);
        setName(gameName);
        this.item = item;
        this.itemCount = itemCount;
    }

    public String getItem() {
        return item;
    }

    public int getItemCount() {
        return itemCount;
    }
}
