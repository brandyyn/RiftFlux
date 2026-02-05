package com.zyin.zyinhud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.MouseEvent;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.zyin.zyinhud.keyhandlers.AnimalInfoKeyHandler;
import com.zyin.zyinhud.keyhandlers.CoordinatesKeyHandler;
import com.zyin.zyinhud.keyhandlers.DistanceMeasurerKeyHandler;
import com.zyin.zyinhud.keyhandlers.EatingAidKeyHandler;
import com.zyin.zyinhud.keyhandlers.EnderPearlAidKeyHandler;
import com.zyin.zyinhud.keyhandlers.ItemSelectorKeyHandler;
import com.zyin.zyinhud.keyhandlers.PlayerLocatorKeyHandler;
import com.zyin.zyinhud.keyhandlers.PotionAidKeyHandler;
import com.zyin.zyinhud.keyhandlers.QuickDepositKeyHandler;
import com.zyin.zyinhud.keyhandlers.SafeOverlayKeyHandler;
import com.zyin.zyinhud.keyhandlers.WeaponSwapperKeyHandler;
import com.zyin.zyinhud.keyhandlers.ZyinHUDOptionsKeyHandler;
import com.zyin.zyinhud.mods.Miscellaneous;
import com.zyin.zyinhud.mods.TorchAid;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;

public class ZyinHUDKeyHandlers
{
	private final static Minecraft mc = Minecraft.getMinecraft();
    /**
     * An array of all of Zyin's HUD custom key bindings. Don't reorder them since they are referenced by their position in the array.<br><ul>
     * <li>[0] Animal Info
     * <li>[1] Coordinates
     * <li>[2] Distance Measurer
     * <li>[3] Eating Aid
     * <li>[4] Ender Pearl Aid
     * <li>[5] Player Locator
     * <li>[6] Potion Aid
     * <li>[7] Quick Deposit
     * <li>[8] Safe Overlay
     * <li>[9] Weapon Swapper
     * <li>[10] Zyin's HUD Options
     * <li>[11] Item Selector
     */
    public static final KeyBinding[] KEY_BINDINGS =
	{
	    new KeyBinding(QuickDepositKeyHandler.HotkeyDescription, 	Keyboard.getKeyIndex("X"), 	   ZyinHUD.MODNAME),	//[0]
	    new KeyBinding(ItemSelectorKeyHandler.HotkeyDescription, 	Keyboard.getKeyIndex("TAB"), ZyinHUD.MODNAME),	//[1]
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


        //Middle click
        if(event.button == 2)
        {
        	if(event.buttonstate)
        	{
            	Miscellaneous.OnMiddleClick();
        	}
        }
    }


    @SubscribeEvent
    public void ClientTickEvent(ClientTickEvent event)
    {
    	//This tick handler is to overcome the GuiScreen + KeyInputEvent limitation
    	//for Coordinates and QuickDeposit
		if(Keyboard.getEventKey() == KEY_BINDINGS[0].getKeyCode())
			QuickDepositKeyHandler.ClientTickEvent(event);

		//since this method is in the ClientTickEvent, it'll overcome the GuiScreen limitation of not handling mouse clicks
		FireUseBlockEvents();
    }


    private static boolean useBlockButtonPreviouslyDown = false;

    private static void FireUseBlockEvents()
    {
    	//.keyBindUseItem		isButtonDown()
    	//keyboard key = postive
    	//forward click = -96	4
    	//backward click = -97	3
    	//middle click = -98	2
    	//right click = -99		1
    	//left click = -100		0

    	boolean useBlockButtonDown;

    	if(mc.gameSettings.keyBindUseItem.getKeyCode() < 0)	//the Use Block hotkey is bound to the mouse
    	{
            useBlockButtonDown = Mouse.isButtonDown(100 + mc.gameSettings.keyBindUseItem.getKeyCode());
    	}
    	else	//the Use Block hotkey is bound to the keyboard
    	{
            useBlockButtonDown = Keyboard.isKeyDown(mc.gameSettings.keyBindUseItem.getKeyCode());
    	}

    	if(useBlockButtonDown == true & useBlockButtonPreviouslyDown == false)
    		OnUseBlockPressed();
    	else if(useBlockButtonDown == false & useBlockButtonPreviouslyDown == true)
    		OnUseBlockReleased();

    	useBlockButtonPreviouslyDown = useBlockButtonDown;
    }
    private static void OnUseBlockPressed()
    {
    }
    private static void OnUseBlockReleased()
    {
    }
}