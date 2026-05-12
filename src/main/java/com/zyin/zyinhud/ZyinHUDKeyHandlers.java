package com.zyin.zyinhud;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.MouseEvent;

import org.lwjgl.input.Keyboard;

import com.zyin.zyinhud.keyhandlers.ItemSelectorKeyHandler;
import com.zyin.zyinhud.keyhandlers.QuickDepositKeyHandler;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;

public class ZyinHUDKeyHandlers
{
    /**
     * An array of all of Zyin's HUD custom key bindings. Don't reorder them since they are referenced by their position in the array.<br><ul>
     * <li>[0] Quick Deposit
     * <li>[1] Item Selector
     */
    public static final KeyBinding[] KEY_BINDINGS =
	{
	    new KeyBinding(QuickDepositKeyHandler.HotkeyDescription, 	Keyboard.getKeyIndex("X"), 	   "RiftFlux"),	//[0]
	    new KeyBinding(ItemSelectorKeyHandler.HotkeyDescription, 	Keyboard.getKeyIndex("TAB"), "RiftFlux"),	//[1]
	};

    public static final ZyinHUDKeyHandlers instance = new ZyinHUDKeyHandlers();

	public ZyinHUDKeyHandlers()
	{
		for(KeyBinding keyBinding : KEY_BINDINGS)
			ClientRegistry.registerKeyBinding(keyBinding);
	}

	@SubscribeEvent
	public void KeyInputEvent(KeyInputEvent event)
	{
		//KeyInputEvent will not fire when looking at a GuiScreen - 1.7.2

		//if 2 KeyBindings have the same hotkey, only 1 will be flagged as "pressed" in getIsKeyPressed(),
		//which one ends up getting pressed in that scenario is undetermined

		if(Keyboard.getEventKey() == ZyinHUDKeyHandlers.KEY_BINDINGS[1].getKeyCode() && !Keyboard.getEventKeyState())	//on key released
			ItemSelectorKeyHandler.Released(event);

	}

    @SubscribeEvent
    public void MouseEvent(MouseEvent event)
    {
    	//event.buttonstate = true if pressed, false if released
    	//event.button = -1 = mouse moved
    	//event.button =  0 = Left click
    	//event.button =  1 = Right click
    	//event.button =  2 = Middle click
    	//event.dwheel =    0 = mouse moved
    	//event.dwheel =  120 = mouse wheel up
    	//event.dwheel = -120 = mouse wheel down

    	if(event.dx != 0 || event.dy != 0)	//mouse movement event
    		return;

    	//Mouse wheel scroll
        if(event.dwheel != 0)
        {
        	if(KEY_BINDINGS[1].getIsKeyPressed())
        		ItemSelectorKeyHandler.OnMouseWheelScroll(event);
        }


    }


    @SubscribeEvent
    public void ClientTickEvent(ClientTickEvent event)
    {
    	//This tick handler is to overcome the GuiScreen + KeyInputEvent limitation
    	//for QuickDeposit
		if(Keyboard.getEventKey() == KEY_BINDINGS[0].getKeyCode())
			QuickDepositKeyHandler.ClientTickEvent(event);
    }
}
