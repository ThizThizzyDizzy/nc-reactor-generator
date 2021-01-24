package planner.vr;
import java.awt.Color;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.function.Function;
import multiblock.Direction;
import org.joml.Matrix4f;
import org.joml.Matrix4x3f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.openvr.HmdMatrix34;
import org.lwjgl.openvr.HmdMatrix44;
import org.lwjgl.openvr.OpenVR;
import org.lwjgl.openvr.Texture;
import org.lwjgl.openvr.TrackedDevicePose;
import static org.lwjgl.openvr.VR.*;
import static org.lwjgl.openvr.VRCompositor.*;
import org.lwjgl.openvr.VREvent;
import static org.lwjgl.openvr.VRSystem.VRSystem_GetEyeToHeadTransform;
import static org.lwjgl.openvr.VRSystem.VRSystem_GetProjectionMatrix;
import static org.lwjgl.openvr.VRSystem.VRSystem_PollNextEvent;
import planner.Core;
import static planner.Core.helper;
import planner.menu.MenuMain;
import planner.vr.menu.VRMenuMain;
import simplelibrary.game.Framebuffer;
import simplelibrary.game.GameHelper;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.gui.Menu;
public class VRCore{
    public static ArrayList<Long> VRFPStracker = new ArrayList<>();
    public static Framebuffer leftEyeBuffer;
    public static Framebuffer rightEyeBuffer;
    public static final int vrWidth = 2016;//*15/32;
    public static final int vrHeight = 2240;//*15/32;
    private static ArrayList<ArrayList<Integer>> pressedButtons = new ArrayList<>();
    private static ArrayList<ArrayList<Integer>> touchedButtons = new ArrayList<>();
    private static ArrayList<Boolean> isController = new ArrayList<>();
    public static VRGUI vrgui = new VRGUI();
    private static boolean running = true;
    public static void start(){
        IntBuffer peError = IntBuffer.allocate(1);
        int token = VR_InitInternal(peError, EVRApplicationType_VRApplication_Scene);
        if(peError.get(0)!=0){
            throw new RuntimeException("Failed to initialize VR!\nINIT ERROR SYMBOL: " + VR_GetVRInitErrorAsSymbol(peError.get(0))+"\nINIT ERROR  DESCR: " + VR_GetVRInitErrorAsEnglishDescription(peError.get(0)));
        }
        OpenVR.create(token);
        Core.gui.open(new Menu(Core.gui, Core.gui.menu){
            @Override
            public void onGUIClosed(){
                running = false;
                OpenVR.destroy();
                VR_ShutdownInternal();
            }
            @Override
            public void tick(){
                VRCore.tick();
                //<editor-fold defaultstate="collapsed" desc="Process VREvents">
                VREvent event;
                while(running&&VRSystem_PollNextEvent(event = VREvent.malloc())){
                    int type = event.eventType();
                    System.out.println("VR Event type="+type);
                    if(type==EVREventType_VREvent_None)System.out.println("- None");
                    if(type==EVREventType_VREvent_TrackedDeviceActivated)System.out.println("- TrackedDeviceActivated");
                    if(type==EVREventType_VREvent_TrackedDeviceDeactivated)System.out.println("- TrackedDeviceDeactivated");
                    if(type==EVREventType_VREvent_TrackedDeviceUpdated)System.out.println("- TrackedDeviceUpdated");
                    if(type==EVREventType_VREvent_TrackedDeviceUserInteractionStarted)System.out.println("- TrackedDeviceUserInteractionStarted");
                    if(type==EVREventType_VREvent_TrackedDeviceUserInteractionEnded)System.out.println("- TrackedDeviceUserInteractionEnded");
                    if(type==EVREventType_VREvent_IpdChanged)System.out.println("- IpdChanged");
                    if(type==EVREventType_VREvent_EnterStandbyMode)System.out.println("- EnterStandbyMode");
                    if(type==EVREventType_VREvent_LeaveStandbyMode)System.out.println("- LeaveStandbyMode");
                    if(type==EVREventType_VREvent_TrackedDeviceRoleChanged)System.out.println("- TrackedDeviceRoleChanged");
                    if(type==EVREventType_VREvent_WatchdogWakeUpRequested)System.out.println("- WatchdogWakeUpRequested");
                    if(type==EVREventType_VREvent_LensDistortionChanged)System.out.println("- LensDistortionChanged");
                    if(type==EVREventType_VREvent_PropertyChanged)System.out.println("- PropertyChanged");
                    if(type==EVREventType_VREvent_WirelessDisconnect)System.out.println("- WirelessDisconnect");
                    if(type==EVREventType_VREvent_WirelessReconnect)System.out.println("- WirelessReconnect");
                    if(type==EVREventType_VREvent_ButtonPress){
                        System.out.println("- ButtonPress "+event.trackedDeviceIndex()+" "+event.data().controller().button());
                        while(event.trackedDeviceIndex()>=pressedButtons.size())pressedButtons.add(new ArrayList<>());
                        pressedButtons.get(event.trackedDeviceIndex()).add(event.data().controller().button());
                        while(event.trackedDeviceIndex()>=isController.size())isController.add(false);
                        if(event.trackedDeviceIndex()>0)isController.set(event.trackedDeviceIndex(), true);
                        vrgui.onKeyEvent(event.trackedDeviceIndex(), event.data().controller().button(), true);
                    }
                    if(type==EVREventType_VREvent_ButtonUnpress){
                        System.out.println("- ButtonUnpress "+event.trackedDeviceIndex()+" "+event.data().controller().button());
                        while(event.trackedDeviceIndex()>=pressedButtons.size())pressedButtons.add(new ArrayList<>());
                        pressedButtons.get(event.trackedDeviceIndex()).remove((Integer)event.data().controller().button());
                        while(event.trackedDeviceIndex()>=isController.size())isController.add(false);
                        if(event.trackedDeviceIndex()>0)isController.set(event.trackedDeviceIndex(), true);
                        vrgui.onKeyEvent(event.trackedDeviceIndex(), event.data().controller().button(), false);
                    }
                    if(type==EVREventType_VREvent_ButtonTouch){
                        System.out.println("- ButtonTouch "+event.trackedDeviceIndex()+" "+event.data().controller().button());
                        while(event.trackedDeviceIndex()>=touchedButtons.size())touchedButtons.add(new ArrayList<>());
                        touchedButtons.get(event.trackedDeviceIndex()).add(event.data().controller().button());
                        while(event.trackedDeviceIndex()>=isController.size())isController.add(false);
                        if(event.trackedDeviceIndex()>0)isController.set(event.trackedDeviceIndex(), true);
                    }
                    if(type==EVREventType_VREvent_ButtonUntouch){
                        System.out.println("- ButtonUntouch "+event.trackedDeviceIndex()+" "+event.data().controller().button());
                        while(event.trackedDeviceIndex()>=touchedButtons.size())touchedButtons.add(new ArrayList<>());
                        touchedButtons.get(event.trackedDeviceIndex()).remove((Integer)event.data().controller().button());
                        while(event.trackedDeviceIndex()>=isController.size())isController.add(false);
                        if(event.trackedDeviceIndex()>0)isController.set(event.trackedDeviceIndex(), true);
                    }
                    if(type==EVREventType_VREvent_DualAnalog_Press)System.out.println("- DualAnalog_Press");
                    if(type==EVREventType_VREvent_DualAnalog_Unpress)System.out.println("- DualAnalog_Unpress");
                    if(type==EVREventType_VREvent_DualAnalog_Touch)System.out.println("- DualAnalog_Touch");
                    if(type==EVREventType_VREvent_DualAnalog_Untouch)System.out.println("- DualAnalog_Untouch");
                    if(type==EVREventType_VREvent_DualAnalog_Move)System.out.println("- DualAnalog_Move");
                    if(type==EVREventType_VREvent_DualAnalog_ModeSwitch1)System.out.println("- DualAnalog_ModeSwitch1");
                    if(type==EVREventType_VREvent_DualAnalog_ModeSwitch2)System.out.println("- DualAnalog_ModeSwitch2");
                    if(type==EVREventType_VREvent_DualAnalog_Cancel)System.out.println("- DualAnalog_Cancel");
                    if(type==EVREventType_VREvent_MouseMove)System.out.println("- MouseMove");
                    if(type==EVREventType_VREvent_MouseButtonDown)System.out.println("- MouseButtonDown");
                    if(type==EVREventType_VREvent_MouseButtonUp)System.out.println("- MouseButtonUp");
                    if(type==EVREventType_VREvent_FocusEnter)System.out.println("- FocusEnter");
                    if(type==EVREventType_VREvent_FocusLeave)System.out.println("- FocusLeave");
                    if(type==EVREventType_VREvent_ScrollDiscrete)System.out.println("- ScrollDiscrete");
                    if(type==EVREventType_VREvent_TouchPadMove)System.out.println("- TouchPadMove");
                    if(type==EVREventType_VREvent_OverlayFocusChanged)System.out.println("- OverlayFocusChanged");
                    if(type==EVREventType_VREvent_ReloadOverlays)System.out.println("- ReloadOverlays");
                    if(type==EVREventType_VREvent_ScrollSmooth)System.out.println("- ScrollSmooth");
                    if(type==EVREventType_VREvent_InputFocusCaptured)System.out.println("- InputFocusCaptured");
                    if(type==EVREventType_VREvent_InputFocusReleased)System.out.println("- InputFocusReleased");
                    if(type==EVREventType_VREvent_SceneFocusLost)System.out.println("- SceneFocusLost");
                    if(type==EVREventType_VREvent_SceneFocusGained)System.out.println("- SceneFocusGained");
                    if(type==EVREventType_VREvent_SceneApplicationChanged)System.out.println("- SceneApplicationChanged");
                    if(type==EVREventType_VREvent_SceneFocusChanged)System.out.println("- SceneFocusChanged");
                    if(type==EVREventType_VREvent_InputFocusChanged)System.out.println("- InputFocusChanged");
                    if(type==EVREventType_VREvent_SceneApplicationSecondaryRenderingStarted)System.out.println("- SceneApplicationSecondaryRenderingStarted");
                    if(type==EVREventType_VREvent_SceneApplicationUsingWrongGraphicsAdapter)System.out.println("- SceneApplicationUsingWrongGraphicsAdapter");
                    if(type==EVREventType_VREvent_ActionBindingReloaded)System.out.println("- ActionBindingReloaded");
                    if(type==EVREventType_VREvent_HideRenderModels)System.out.println("- HideRenderModels");
                    if(type==EVREventType_VREvent_ShowRenderModels)System.out.println("- ShowRenderModels");
                    if(type==EVREventType_VREvent_ConsoleOpened)System.out.println("- ConsoleOpened");
                    if(type==EVREventType_VREvent_ConsoleClosed)System.out.println("- ConsoleClosed");
                    if(type==EVREventType_VREvent_OverlayShown)System.out.println("- OverlayShown");
                    if(type==EVREventType_VREvent_OverlayHidden)System.out.println("- OverlayHidden");
                    if(type==EVREventType_VREvent_DashboardActivated)System.out.println("- DashboardActivated");
                    if(type==EVREventType_VREvent_DashboardDeactivated)System.out.println("- DashboardDeactivated");
                    if(type==EVREventType_VREvent_DashboardRequested)System.out.println("- DashboardRequested");
                    if(type==EVREventType_VREvent_ResetDashboard)System.out.println("- ResetDashboard");
                    if(type==EVREventType_VREvent_RenderToast)System.out.println("- RenderToast");
                    if(type==EVREventType_VREvent_ImageLoaded)System.out.println("- ImageLoaded");
                    if(type==EVREventType_VREvent_ShowKeyboard)System.out.println("- ShowKeyboard");
                    if(type==EVREventType_VREvent_HideKeyboard)System.out.println("- HideKeyboard");
                    if(type==EVREventType_VREvent_OverlayGamepadFocusGained)System.out.println("- OverlayGamepadFocusGained");
                    if(type==EVREventType_VREvent_OverlayGamepadFocusLost)System.out.println("- OverlayGamepadFocusLost");
                    if(type==EVREventType_VREvent_OverlaySharedTextureChanged)System.out.println("- OverlaySharedTextureChanged");
                    if(type==EVREventType_VREvent_ScreenshotTriggered)System.out.println("- ScreenshotTriggered");
                    if(type==EVREventType_VREvent_ImageFailed)System.out.println("- ImageFailed");
                    if(type==EVREventType_VREvent_DashboardOverlayCreated)System.out.println("- DashboardOverlayCreated");
                    if(type==EVREventType_VREvent_SwitchGamepadFocus)System.out.println("- SwitchGamepadFocus");
                    if(type==EVREventType_VREvent_RequestScreenshot)System.out.println("- RequestScreenshot");
                    if(type==EVREventType_VREvent_ScreenshotTaken)System.out.println("- ScreenshotTaken");
                    if(type==EVREventType_VREvent_ScreenshotFailed)System.out.println("- ScreenshotFailed");
                    if(type==EVREventType_VREvent_SubmitScreenshotToDashboard)System.out.println("- SubmitScreenshotToDashboard");
                    if(type==EVREventType_VREvent_ScreenshotProgressToDashboard)System.out.println("- ScreenshotProgressToDashboard");
                    if(type==EVREventType_VREvent_PrimaryDashboardDeviceChanged)System.out.println("- PrimaryDashboardDeviceChanged");
                    if(type==EVREventType_VREvent_RoomViewShown)System.out.println("- RoomViewShown");
                    if(type==EVREventType_VREvent_RoomViewHidden)System.out.println("- RoomViewHidden");
                    if(type==EVREventType_VREvent_ShowUI)System.out.println("- ShowUI");
                    if(type==EVREventType_VREvent_ShowDevTools)System.out.println("- ShowDevTools");
                    if(type==EVREventType_VREvent_Notification_Shown)System.out.println("- Notification_Shown");
                    if(type==EVREventType_VREvent_Notification_Hidden)System.out.println("- Notification_Hidden");
                    if(type==EVREventType_VREvent_Notification_BeginInteraction)System.out.println("- Notification_BeginInteraction");
                    if(type==EVREventType_VREvent_Notification_Destroyed)System.out.println("- Notification_Destroyed");
                    if(type==EVREventType_VREvent_Quit){
                        System.out.println("- Quit");
                        gui.open(new MenuMain(gui));
                    }
                    if(type==EVREventType_VREvent_ProcessQuit)System.out.println("- ProcessQuit");
                    if(type==EVREventType_VREvent_QuitAborted_UserPrompt)System.out.println("- QuitAborted_UserPrompt");
                    if(type==EVREventType_VREvent_QuitAcknowledged)System.out.println("- QuitAcknowledged");
                    if(type==EVREventType_VREvent_DriverRequestedQuit)System.out.println("- DriverRequestedQuit");
                    if(type==EVREventType_VREvent_RestartRequested)System.out.println("- RestartRequested");
                    if(type==EVREventType_VREvent_ChaperoneDataHasChanged)System.out.println("- ChaperoneDataHasChanged");
                    if(type==EVREventType_VREvent_ChaperoneUniverseHasChanged)System.out.println("- ChaperoneUniverseHasChanged");
                    if(type==EVREventType_VREvent_ChaperoneTempDataHasChanged)System.out.println("- ChaperoneTempDataHasChanged");
                    if(type==EVREventType_VREvent_ChaperoneSettingsHaveChanged)System.out.println("- ChaperoneSettingsHaveChanged");
                    if(type==EVREventType_VREvent_SeatedZeroPoseReset)System.out.println("- SeatedZeroPoseReset");
                    if(type==EVREventType_VREvent_ChaperoneFlushCache)System.out.println("- ChaperoneFlushCache");
                    if(type==EVREventType_VREvent_ChaperoneRoomSetupStarting)System.out.println("- ChaperoneRoomSetupStarting");
                    if(type==EVREventType_VREvent_ChaperoneRoomSetupFinished)System.out.println("- ChaperoneRoomSetupFinished");
                    if(type==EVREventType_VREvent_AudioSettingsHaveChanged)System.out.println("- AudioSettingsHaveChanged");
                    if(type==EVREventType_VREvent_BackgroundSettingHasChanged)System.out.println("- BackgroundSettingHasChanged");
                    if(type==EVREventType_VREvent_CameraSettingsHaveChanged)System.out.println("- CameraSettingsHaveChanged");
                    if(type==EVREventType_VREvent_ReprojectionSettingHasChanged)System.out.println("- ReprojectionSettingHasChanged");
                    if(type==EVREventType_VREvent_ModelSkinSettingsHaveChanged)System.out.println("- ModelSkinSettingsHaveChanged");
                    if(type==EVREventType_VREvent_EnvironmentSettingsHaveChanged)System.out.println("- EnvironmentSettingsHaveChanged");
                    if(type==EVREventType_VREvent_PowerSettingsHaveChanged)System.out.println("- PowerSettingsHaveChanged");
                    if(type==EVREventType_VREvent_EnableHomeAppSettingsHaveChanged)System.out.println("- EnableHomeAppSettingsHaveChanged");
                    if(type==EVREventType_VREvent_SteamVRSectionSettingChanged)System.out.println("- SteamVRSectionSettingChanged");
                    if(type==EVREventType_VREvent_LighthouseSectionSettingChanged)System.out.println("- LighthouseSectionSettingChanged");
                    if(type==EVREventType_VREvent_NullSectionSettingChanged)System.out.println("- NullSectionSettingChanged");
                    if(type==EVREventType_VREvent_UserInterfaceSectionSettingChanged)System.out.println("- UserInterfaceSectionSettingChanged");
                    if(type==EVREventType_VREvent_NotificationsSectionSettingChanged)System.out.println("- NotificationsSectionSettingChanged");
                    if(type==EVREventType_VREvent_KeyboardSectionSettingChanged)System.out.println("- KeyboardSectionSettingChanged");
                    if(type==EVREventType_VREvent_PerfSectionSettingChanged)System.out.println("- PerfSectionSettingChanged");
                    if(type==EVREventType_VREvent_DashboardSectionSettingChanged)System.out.println("- DashboardSectionSettingChanged");
                    if(type==EVREventType_VREvent_WebInterfaceSectionSettingChanged)System.out.println("- WebInterfaceSectionSettingChanged");
                    if(type==EVREventType_VREvent_TrackersSectionSettingChanged)System.out.println("- TrackersSectionSettingChanged");
                    if(type==EVREventType_VREvent_LastKnownSectionSettingChanged)System.out.println("- LastKnownSectionSettingChanged");
                    if(type==EVREventType_VREvent_DismissedWarningsSectionSettingChanged)System.out.println("- DismissedWarningsSectionSettingChanged");
                    if(type==EVREventType_VREvent_StatusUpdate)System.out.println("- StatusUpdate");
                    if(type==EVREventType_VREvent_WebInterface_InstallDriverCompleted)System.out.println("- WebInterface_InstallDriverCompleted");
                    if(type==EVREventType_VREvent_MCImageUpdated)System.out.println("- MCImageUpdated");
                    if(type==EVREventType_VREvent_FirmwareUpdateStarted)System.out.println("- FirmwareUpdateStarted");
                    if(type==EVREventType_VREvent_FirmwareUpdateFinished)System.out.println("- FirmwareUpdateFinished");
                    if(type==EVREventType_VREvent_KeyboardClosed)System.out.println("- KeyboardClosed");
                    if(type==EVREventType_VREvent_KeyboardCharInput)System.out.println("- KeyboardCharInput");
                    if(type==EVREventType_VREvent_KeyboardDone)System.out.println("- KeyboardDone");
                    if(type==EVREventType_VREvent_ApplicationTransitionStarted)System.out.println("- ApplicationTransitionStarted");
                    if(type==EVREventType_VREvent_ApplicationTransitionAborted)System.out.println("- ApplicationTransitionAborted");
                    if(type==EVREventType_VREvent_ApplicationTransitionNewAppStarted)System.out.println("- ApplicationTransitionNewAppStarted");
                    if(type==EVREventType_VREvent_ApplicationListUpdated)System.out.println("- ApplicationListUpdated");
                    if(type==EVREventType_VREvent_ApplicationMimeTypeLoad)System.out.println("- ApplicationMimeTypeLoad");
                    if(type==EVREventType_VREvent_ApplicationTransitionNewAppLaunchComplete)System.out.println("- ApplicationTransitionNewAppLaunchComplete");
                    if(type==EVREventType_VREvent_ProcessConnected)System.out.println("- ProcessConnected");
                    if(type==EVREventType_VREvent_ProcessDisconnected)System.out.println("- ProcessDisconnected");
                    if(type==EVREventType_VREvent_Compositor_MirrorWindowShown)System.out.println("- Compositor_MirrorWindowShown");
                    if(type==EVREventType_VREvent_Compositor_MirrorWindowHidden)System.out.println("- Compositor_MirrorWindowHidden");
                    if(type==EVREventType_VREvent_Compositor_ChaperoneBoundsShown)System.out.println("- Compositor_ChaperoneBoundsShown");
                    if(type==EVREventType_VREvent_Compositor_ChaperoneBoundsHidden)System.out.println("- Compositor_ChaperoneBoundsHidden");
                    if(type==EVREventType_VREvent_Compositor_DisplayDisconnected)System.out.println("- Compositor_DisplayDisconnected");
                    if(type==EVREventType_VREvent_Compositor_DisplayReconnected)System.out.println("- Compositor_DisplayReconnected");
                    if(type==EVREventType_VREvent_Compositor_HDCPError)System.out.println("- Compositor_HDCPError");
                    if(type==EVREventType_VREvent_Compositor_ApplicationNotResponding)System.out.println("- Compositor_ApplicationNotResponding");
                    if(type==EVREventType_VREvent_Compositor_ApplicationResumed)System.out.println("- Compositor_ApplicationResumed");
                    if(type==EVREventType_VREvent_Compositor_OutOfVideoMemory)System.out.println("- Compositor_OutOfVideoMemory");
                    if(type==EVREventType_VREvent_TrackedCamera_StartVideoStream)System.out.println("- TrackedCamera_StartVideoStream");
                    if(type==EVREventType_VREvent_TrackedCamera_StopVideoStream)System.out.println("- TrackedCamera_StopVideoStream");
                    if(type==EVREventType_VREvent_TrackedCamera_PauseVideoStream)System.out.println("- TrackedCamera_PauseVideoStream");
                    if(type==EVREventType_VREvent_TrackedCamera_ResumeVideoStream)System.out.println("- TrackedCamera_ResumeVideoStream");
                    if(type==EVREventType_VREvent_TrackedCamera_EditingSurface)System.out.println("- TrackedCamera_EditingSurface");
                    if(type==EVREventType_VREvent_PerformanceTest_EnableCapture)System.out.println("- PerformanceTest_EnableCapture");
                    if(type==EVREventType_VREvent_PerformanceTest_DisableCapture)System.out.println("- PerformanceTest_DisableCapture");
                    if(type==EVREventType_VREvent_PerformanceTest_FidelityLevel)System.out.println("- PerformanceTest_FidelityLevel");
                    if(type==EVREventType_VREvent_MessageOverlay_Closed)System.out.println("- MessageOverlay_Closed");
                    if(type==EVREventType_VREvent_MessageOverlayCloseRequested)System.out.println("- MessageOverlayCloseRequested");
                    if(type==EVREventType_VREvent_Input_HapticVibration)System.out.println("- Input_HapticVibration");
                    if(type==EVREventType_VREvent_Input_BindingLoadFailed)System.out.println("- Input_BindingLoadFailed");
                    if(type==EVREventType_VREvent_Input_BindingLoadSuccessful)System.out.println("- Input_BindingLoadSuccessful");
                    if(type==EVREventType_VREvent_Input_ActionManifestReloaded)System.out.println("- Input_ActionManifestReloaded");
                    if(type==EVREventType_VREvent_Input_ActionManifestLoadFailed)System.out.println("- Input_ActionManifestLoadFailed");
                    if(type==EVREventType_VREvent_Input_ProgressUpdate)System.out.println("- Input_ProgressUpdate");
                    if(type==EVREventType_VREvent_Input_TrackerActivated)System.out.println("- Input_TrackerActivated");
                    if(type==EVREventType_VREvent_Input_BindingsUpdated)System.out.println("- Input_BindingsUpdated");
                    if(type==EVREventType_VREvent_SpatialAnchors_PoseUpdated)System.out.println("- SpatialAnchors_PoseUpdated");
                    if(type==EVREventType_VREvent_SpatialAnchors_DescriptorUpdated)System.out.println("- SpatialAnchors_DescriptorUpdated");
                    if(type==EVREventType_VREvent_SpatialAnchors_RequestPoseUpdate)System.out.println("- SpatialAnchors_RequestPoseUpdate");
                    if(type==EVREventType_VREvent_SpatialAnchors_RequestDescriptorUpdate)System.out.println("- SpatialAnchors_RequestDescriptorUpdate");
                    if(type==EVREventType_VREvent_SystemReport_Started)System.out.println("- SystemReport_Started");
                    if(type==EVREventType_VREvent_VendorSpecific_Reserved_Start)System.out.println("- VendorSpecific_Reserved_Start");
                    if(type==EVREventType_VREvent_VendorSpecific_Reserved_End)System.out.println("- VendorSpecific_Reserved_End");
                }
    //</editor-fold>
            }
            @Override
            public void render(int millisSinceLastTick){
                super.render(millisSinceLastTick);
                ImageStash.instance.bindTexture(0);
                GL11.glColor4f(1, 1, 1, 1);
                if(leftEyeBuffer==null){
                    leftEyeBuffer = new Framebuffer(helper, "Left Eye Framebuffer", vrWidth, vrHeight);
                }
                if(rightEyeBuffer==null){
                    rightEyeBuffer = new Framebuffer(helper, "Right Eye Framebuffer", vrWidth, vrHeight);
                }
                Color background = Core.theme.getBackgroundColor();
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                TrackedDevicePose.Buffer tdpb = TrackedDevicePose.create(k_unMaxTrackedDeviceCount);
                TrackedDevicePose.Buffer tdpb2 = TrackedDevicePose.create(k_unMaxTrackedDeviceCount);
                VRCompositor_WaitGetPoses(tdpb, tdpb2);
                helper.renderTargetFramebuffer(leftEyeBuffer.stash.getBuffer(leftEyeBuffer.name), GameHelper.MODE_3D, leftEyeBuffer.width, leftEyeBuffer.height, 1);
                GL11.glClearColor(background.getRed()/255f, background.getGreen()/255f, background.getBlue()/255f, 1);
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                GL11.glColor4f(1, 1, 1, 1);
                Matrix4f[] projectionMatrices = new Matrix4f[2];
                for(int i = 0; i<2; i++){
                    projectionMatrices[i] = convert(VRSystem_GetProjectionMatrix(i, .01f, 1000, HmdMatrix44.calloc())).transpose();//near and far
                }
                Matrix4f[] eyeMatrices = new Matrix4f[2];
                TrackedDevicePose hmdPose = tdpb.get(k_unTrackedDeviceIndex_Hmd);
                Matrix4f headPose = new Matrix4f();
                if(hmdPose.bDeviceIsConnected()&&hmdPose.bPoseIsValid()){
                    headPose = new Matrix4f(convert(hmdPose.mDeviceToAbsoluteTracking())).invert();
                }
                for(int i = 0; i<2; i++){
                    eyeMatrices[i] = new Matrix4f(convert(VRSystem_GetEyeToHeadTransform(i, HmdMatrix34.calloc()))).invert().mul(headPose);
                }
                GL11.glMatrixMode(GL11.GL_PROJECTION);
                GL11.glLoadMatrixf(projectionMatrices[0].get(new float[16]));
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glLoadMatrixf(eyeMatrices[0].get(new float[16]));
                VRCore.render(tdpb);
                helper.renderTargetFramebuffer(0, 0, 0, 0, 0);//clearFramebuffer()
                GL11.glColor4f(1, 1, 1, 1);
                Texture textureLeft = Texture.create();
                textureLeft.set(leftEyeBuffer.getTexture(), ETextureType_TextureType_OpenGL, EColorSpace_ColorSpace_Auto);
                int left = VRCompositor_Submit(EVREye_Eye_Left, textureLeft, null, EVRSubmitFlags_Submit_Default);
                //<editor-fold defaultstate="collapsed" desc="Left Eye Error">
                switch(left){
                    case EVRCompositorError_VRCompositorError_None:
                        break;
                    case EVRCompositorError_VRCompositorError_RequestFailed:
                        System.out.println("LRequestFailed");
                        break;
                    case EVRCompositorError_VRCompositorError_IncompatibleVersion:
                        System.out.println("LIncompatibleVersion");
                        break;
                    case EVRCompositorError_VRCompositorError_DoNotHaveFocus:
                        System.out.println("LDoNotHaveFocus");
                        break;
                    case EVRCompositorError_VRCompositorError_InvalidTexture:
                        System.out.println("LInvalidTexture");
                        break;
                    case EVRCompositorError_VRCompositorError_IsNotSceneApplication:
                        System.out.println("LIsNotSceneApplication");
                        break;
                    case EVRCompositorError_VRCompositorError_TextureIsOnWrongDevice:
                        System.out.println("LTextureIsOnWrongDevice");
                        break;
                    case EVRCompositorError_VRCompositorError_TextureUsesUnsupportedFormat:
                        System.out.println("LTextureUsesUnsupportedFormat");
                        break;
                    case EVRCompositorError_VRCompositorError_SharedTexturesNotSupported:
                        System.out.println("LSharedTexturesNotSupported");
                        break;
                    case EVRCompositorError_VRCompositorError_IndexOutOfRange:
                        System.out.println("LIndexOutOfRange");
                        break;
                    case EVRCompositorError_VRCompositorError_AlreadySubmitted:
                        System.out.println("LAlreadySubmitted");
                        break;
                    case EVRCompositorError_VRCompositorError_InvalidBounds:
                        System.out.println("LInvalidBounds");
                        break;
                }
    //</editor-fold>
                helper.renderTargetFramebuffer(rightEyeBuffer.stash.getBuffer(rightEyeBuffer.name), GameHelper.MODE_3D, rightEyeBuffer.width, rightEyeBuffer.height, 1);
                GL11.glClearColor(background.getRed()/255f, background.getGreen()/255f, background.getBlue()/255f, 1);
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                GL11.glColor4f(1, 1, 1, 1);
                GL11.glMatrixMode(GL11.GL_PROJECTION);
                GL11.glLoadMatrixf(projectionMatrices[1].get(new float[16]));
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glLoadMatrixf(eyeMatrices[1].get(new float[16]));
                VRCore.render(tdpb2);
                helper.renderTargetFramebuffer(0, 0, 0, 0, 0);//clearFramebuffer()
                GL11.glColor4f(1, 1, 1, 1);
                Texture textureRight = Texture.create();
                textureRight.set(rightEyeBuffer.getTexture(), ETextureType_TextureType_OpenGL, EColorSpace_ColorSpace_Auto);
                int right = VRCompositor_Submit(EVREye_Eye_Right, textureRight, null, EVRSubmitFlags_Submit_Default);
                //<editor-fold defaultstate="collapsed" desc="Right Eye Error">
                switch(right){
                    case EVRCompositorError_VRCompositorError_None:
                        break;
                    case EVRCompositorError_VRCompositorError_RequestFailed:
                        System.out.println("RRequestFailed");
                        break;
                    case EVRCompositorError_VRCompositorError_IncompatibleVersion:
                        System.out.println("RIncompatibleVersion");
                        break;
                    case EVRCompositorError_VRCompositorError_DoNotHaveFocus:
                        System.out.println("RDoNotHaveFocus");
                        break;
                    case EVRCompositorError_VRCompositorError_InvalidTexture:
                        System.out.println("RInvalidTexture");
                        break;
                    case EVRCompositorError_VRCompositorError_IsNotSceneApplication:
                        System.out.println("RIsNotSceneApplication");
                        break;
                    case EVRCompositorError_VRCompositorError_TextureIsOnWrongDevice:
                        System.out.println("RTextureIsOnWrongDevice");
                        break;
                    case EVRCompositorError_VRCompositorError_TextureUsesUnsupportedFormat:
                        System.out.println("RTextureUsesUnsupportedFormat");
                        break;
                    case EVRCompositorError_VRCompositorError_SharedTexturesNotSupported:
                        System.out.println("RSharedTexturesNotSupported");
                        break;
                    case EVRCompositorError_VRCompositorError_IndexOutOfRange:
                        System.out.println("RIndexOutOfRange");
                        break;
                    case EVRCompositorError_VRCompositorError_AlreadySubmitted:
                        System.out.println("RAlreadySubmitted");
                        break;
                    case EVRCompositorError_VRCompositorError_InvalidBounds:
                        System.out.println("RInvalidBounds");
                        break;
                }
    //</editor-fold>
                VRFPStracker.add(System.currentTimeMillis());
                while(VRFPStracker.get(0)<System.currentTimeMillis()-5_000){
                    VRFPStracker.remove(0);
                }
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glColor4d(1, 1, 1, 1);
                drawRect(0, helper.displayHeight(), helper.displayWidth(), 0, leftEyeBuffer.getTexture());
            }
        });
        vrgui.open(new VRMenuMain(vrgui));
    }
    public static Matrix4f convert(HmdMatrix44 m){
        return new Matrix4f(m.m(0), m.m(1), m.m(2), m.m(3), m.m(4), m.m(5), m.m(6), m.m(7), m.m(8), m.m(9), m.m(10), m.m(11), m.m(12), m.m(13), m.m(14), m.m(15));//no convert
    }
    public static Matrix4x3f convert(HmdMatrix34 m){
        return new Matrix4x3f(m.m(0), m.m(4), m.m(8), m.m(1), m.m(5), m.m(9), m.m(2), m.m(6), m.m(10), m.m(3), m.m(7), m.m(11));//converts to column-major
//        return new Matrix4x3f(m.m(0), m.m(1), m.m(2), m.m(3), m.m(4), m.m(5), m.m(6), m.m(7), m.m(8), m.m(9), m.m(10), m.m(11));//no convert
    }
    public static long getVRFPS(){
        return VRFPStracker.size()/5;
    }
    public static void tick(){
        vrgui.tick();
    }
    public static void render(TrackedDevicePose.Buffer tdpb){
        vrgui.render(tdpb);
        GL11.glColor4f(1, 1, 1, 1);
        //<editor-fold defaultstate="collapsed" desc="Tracked Devices">
        for(int i = 1; i<5; i++){
            TrackedDevicePose pose = tdpb.get(i);
            if(pose.bDeviceIsConnected()&&pose.bPoseIsValid()){
                Matrix4f matrix = new Matrix4f(convert(pose.mDeviceToAbsoluteTracking()));
                GL11.glPushMatrix();
                GL11.glMultMatrixf(matrix.get(new float[16]));
                if(isController.size()>i&&isController.get(i)){
                    GL11.glColor4f(0, 1, 0, 1);
                    drawCube(-.01f, -.01f, -.01f, .01f, .01f, .01f, 0);
                }else{
                    GL11.glColor4f(1, 0, 0, 1);
                    GL11.glBegin(GL11.GL_QUADS);
                    GL11.glVertex3d(-.01, -.01, 0);
                    GL11.glVertex3d(.01, -.01, 0);
                    GL11.glVertex3d(.01, .01, 0);
                    GL11.glVertex3d(-.01, .01, 0);
                    GL11.glEnd();
                }
                GL11.glPopMatrix();
            }
        }
//</editor-fold>
    }
    /**
     * Draws a cube using one texture for all sides
     * @param minX the lower X boundary
     * @param minY the lower Y boundary
     * @param minZ the lower Z boundary
     * @param maxX the upper X boundary
     * @param maxY the upper Y boundary
     * @param maxZ the upper Z boundary
     * @param texture the texture used to render the cube
     */
    public static void drawCube(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, int texture){
        drawCube(minX, minY, minZ, maxX, maxY, maxZ, texture, (t) -> {
            return true;
        });
    }
    /**
     * Draws a cube using one texture for all sides
     * @param minX the lower X boundary
     * @param minY the lower Y boundary
     * @param minZ the lower Z boundary
     * @param maxX the upper X boundary
     * @param maxY the upper Y boundary
     * @param maxZ the upper Z boundary
     * @param texture the texture used to render the cube
     * @param faceRenderFunc A function that defines if each face should render (Given XYZ offset)
     */
    public static void drawCube(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, int texture, Function<Direction, Boolean> faceRenderFunc){
        boolean px = faceRenderFunc.apply(Direction.PX);
        boolean py = faceRenderFunc.apply(Direction.PY);
        boolean pz = faceRenderFunc.apply(Direction.PZ);
        boolean nx = faceRenderFunc.apply(Direction.NX);
        boolean ny = faceRenderFunc.apply(Direction.NY);
        boolean nz = faceRenderFunc.apply(Direction.NZ);
        if(!px&&!py&&!pz&&!nx&&!ny&&!nz)return;//no faces are actually rendering, save some GL calls
        ImageStash.instance.bindTexture(texture);
        GL11.glBegin(GL11.GL_QUADS);
        //xy +z
        if(pz){
            if(texture!=0)GL11.glTexCoord2d(0, 0);
            GL11.glVertex3d(minX, minY, maxZ);
            if(texture!=0)GL11.glTexCoord2d(0, 1);
            GL11.glVertex3d(maxX, minY, maxZ);
            if(texture!=0)GL11.glTexCoord2d(1, 1);
            GL11.glVertex3d(maxX, maxY, maxZ);
            if(texture!=0)GL11.glTexCoord2d(1, 0);
            GL11.glVertex3d(minX, maxY, maxZ);
        }
        //xy -z
        if(nz){
            if(texture!=0)GL11.glTexCoord2d(0, 0);
            GL11.glVertex3d(minX, minY, minZ);
            if(texture!=0)GL11.glTexCoord2d(0, 1);
            GL11.glVertex3d(minX, maxY, minZ);
            if(texture!=0)GL11.glTexCoord2d(1, 1);
            GL11.glVertex3d(maxX, maxY, minZ);
            if(texture!=0)GL11.glTexCoord2d(1, 0);
            GL11.glVertex3d(maxX, minY, minZ);
        }
        //xz +y
        if(py){
            if(texture!=0)GL11.glTexCoord2d(0, 0);
            GL11.glVertex3d(minX, maxY, minZ);
            if(texture!=0)GL11.glTexCoord2d(0, 1);
            GL11.glVertex3d(minX, maxY, maxZ);
            if(texture!=0)GL11.glTexCoord2d(1, 1);
            GL11.glVertex3d(maxX, maxY, maxZ);
            if(texture!=0)GL11.glTexCoord2d(1, 0);
            GL11.glVertex3d(maxX, maxY, minZ);
        }
        //xz -y
        if(ny){
            if(texture!=0)GL11.glTexCoord2d(0, 0);
            GL11.glVertex3d(minX, minY, minZ);
            if(texture!=0)GL11.glTexCoord2d(0, 1);
            GL11.glVertex3d(maxX, minY, minZ);
            if(texture!=0)GL11.glTexCoord2d(1, 1);
            GL11.glVertex3d(maxX, minY, maxZ);
            if(texture!=0)GL11.glTexCoord2d(1, 0);
            GL11.glVertex3d(minX, minY, maxZ);
        }
        //yz +x
        if(px){
            if(texture!=0)GL11.glTexCoord2d(0, 0);
            GL11.glVertex3d(maxX, minY, minZ);
            if(texture!=0)GL11.glTexCoord2d(0, 1);
            GL11.glVertex3d(maxX, maxY, minZ);
            if(texture!=0)GL11.glTexCoord2d(1, 1);
            GL11.glVertex3d(maxX, maxY, maxZ);
            if(texture!=0)GL11.glTexCoord2d(1, 0);
            GL11.glVertex3d(maxX, minY, maxZ);
        }
        //yz -x
        if(nx){
            if(texture!=0)GL11.glTexCoord2d(0, 0);
            GL11.glVertex3d(minX, minY, minZ);
            if(texture!=0)GL11.glTexCoord2d(0, 1);
            GL11.glVertex3d(minX, minY, maxZ);
            if(texture!=0)GL11.glTexCoord2d(1, 1);
            GL11.glVertex3d(minX, maxY, maxZ);
            if(texture!=0)GL11.glTexCoord2d(1, 0);
            GL11.glVertex3d(minX, maxY, minZ);
        }
        GL11.glEnd();
    }
    public static void drawCubeOutline(double x1, double y1, double z1, double x2, double y2, double z2, double thickness){
        //111 to XYZ
        drawCube(x1, y1, z1, x2, y1+thickness, z1+thickness, 0);
        drawCube(x1, y1, z1, x1+thickness, y2, z1+thickness, 0);
        drawCube(x1, y1, z1, x1+thickness, y1+thickness, z2, 0);
        //X2 to YZ
        drawCube(x2-thickness, y1, z1, x2, y2, z1+thickness, 0);
        drawCube(x2-thickness, y1, z1, x2, y1+thickness, z2, 0);
        //Y2 to XZ
        drawCube(x1, y2-thickness, z1, x2, y2, z1+thickness, 0);
        drawCube(x1, y2-thickness, z1, x1+thickness, y2, z2, 0);
        //Z2 to XY
        drawCube(x1, y1, z2-thickness, x2, y1+thickness, z2, 0);
        drawCube(x1, y1, z2-thickness, x1+thickness, y2, z2, 0);
        //XYZ to 222
        drawCube(x1, y2-thickness, z2-thickness, x2, y2, z2, 0);
        drawCube(x2-thickness, y1, z2-thickness, x2, y2, z2, 0);
        drawCube(x2-thickness, y2-thickness, z1, x2, y2, z2, 0);
    }
    public static double[] rotatePoint(double pointX, double pointY, double degrees, double originX, double originY){
        double rX = pointX-originX, rY = pointY-originY;//Find relative coordinates; easier to rotate around the origin (0, 0) than any other point.
        double rad = degrees*(Math.PI/180);//Angle in radians
        double sin = Math.sin(rad), cos = Math.cos(rad);//Compute the sine and cosine, only two trig operations we need
        //2x2 Rotation matrix:  [Cos(A), -Sin(A)]
        //                      [Sin(A), Cos(A)]
        //Then, with matrix multiplication, our point becomes:
        return new double[]{cos*rX+sin*rY+originX, cos*rY-sin*rX+originY};//Matrix multiplication on the relative coordinates; add origin coords back in before return.
    }
    public static float[] rotatePoint(float pointX, float pointY, float degrees, float originX, float originY){
        float rX = pointX-originX, rY = pointY-originY;//Find relative coordinates; easier to rotate around the origin (0, 0) than any other point.
        double rad = degrees*(Math.PI/180);//Angle in radians
        double sin = Math.sin(rad), cos = Math.cos(rad);//Compute the sine and cosine, only two trig operations we need
        //2x2 Rotation matrix:  [Cos(A), -Sin(A)]
        //                      [Sin(A), Cos(A)]
        //Then, with matrix multiplication, our point becomes:
        return new float[]{(float)(cos*rX+sin*rY+originX), (float)(cos*rY-sin*rX+originY)};//Matrix multiplication on the relative coordinates; add origin coords back in before return.
    }
    public static Vector3f convertPoint(double x, double y, double z, double x0, double y0, double z0, double xRot, double yRot, double zRot){
        x+=x0;
        y+=y0;
        z+=z0;
        double[] xy = VRCore.rotatePoint(x, y, zRot, x0, y0);
        x = xy[0];
        y = xy[1];
        double[] yz = VRCore.rotatePoint(y, z, xRot, y0, z0);
        y = yz[0];
        z = yz[1];
        double[] xz = VRCore.rotatePoint(x, z, yRot, x0, z0);
        x = xz[0];
        z = xz[1];
        return new Vector3f((float)x, (float)y, (float)z);
    }
    public static Vector3f convertPointInverted(double x, double y, double z, double x0, double y0, double z0, double xRot, double yRot, double zRot){
        double[] xz = VRCore.rotatePoint(x, z, -yRot, x0, z0);
        x = xz[0];
        z = xz[1];
        double[] yz = VRCore.rotatePoint(y, z, -xRot, y0, z0);
        y = yz[0];
        z = yz[1];
        double[] xy = VRCore.rotatePoint(x, y, -zRot, x0, y0);
        x = xy[0];
        y = xy[1];
        x-=x0;
        y-=y0;
        z-=z0;
        return new Vector3f((float)x, (float)y, (float)z);
    }
    public static boolean isPointWithinBox(double x, double y, double z, double x0, double y0, double z0, double width, double height, double depth, double xRot, double yRot, double zRot){
        Vector3f p = convertPointInverted(x, y, z, x0, y0, z0, xRot, yRot, zRot);
        return p.x>0&&p.y>0&&p.z>0&&p.x<width&&p.y<height&&p.z<depth;
    }
    public static double distance(Vector3f v1, Vector3d v2){//I know one's float and one's double... don't worry about it
        return Math.sqrt(Math.pow(v1.x-v2.x, 2)+Math.pow(v1.y-v2.y, 2)+Math.pow(v1.z-v2.z, 2));
    }
}