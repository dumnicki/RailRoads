package cam72cam.immersiverailroading.net;

import cam72cam.immersiverailroading.ImmersiveRailroading;
import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.immersiverailroading.tile.TileRailPreview;
import cam72cam.immersiverailroading.util.BufferUtil;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/*
 * Movable rolling stock sync packet
 */
public class PreviewRenderPacket implements IMessage {

	private int dimension;
	private TileRailPreview preview;

	public PreviewRenderPacket() {
		// Reflect constructor
	}

	public PreviewRenderPacket(TileRailPreview preview) {
		this.dimension = preview.getWorldObj().provider.dimensionId;
		this.preview = preview;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(dimension);
		ByteBufUtils.writeTag(buf, preview.writeToNBT(new NBTTagCompound()));
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		dimension = buf.readInt();
		preview = new TileRailPreview();
		preview.readFromNBT(ByteBufUtils.readTag(buf));
	}
	public static class Handler implements IMessageHandler<PreviewRenderPacket, IMessage> {
		@Override
		public IMessage onMessage(PreviewRenderPacket message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(PreviewRenderPacket message, MessageContext ctx) {
			ImmersiveRailroading.proxy.addPreview(message.dimension, message.preview);
		}
	}
}
