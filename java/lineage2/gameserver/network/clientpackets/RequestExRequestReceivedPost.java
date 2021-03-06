/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package lineage2.gameserver.network.clientpackets;

import lineage2.commons.dao.JdbcEntityState;
import lineage2.gameserver.dao.MailDAO;
import lineage2.gameserver.model.Player;
import lineage2.gameserver.model.mail.Mail;
import lineage2.gameserver.network.serverpackets.ExChangePostState;
import lineage2.gameserver.network.serverpackets.ExReplyReceivedPost;
import lineage2.gameserver.network.serverpackets.ExShowReceivedPostList;

/**
 * @author Mobius
 * @version $Revision: 1.0 $
 */
public class RequestExRequestReceivedPost extends L2GameClientPacket
{
	/**
	 * Field postId.
	 */
	private int postId;
	
	/**
	 * Method readImpl.
	 */
	@Override
	protected void readImpl()
	{
		postId = readD();
	}
	
	/**
	 * Method runImpl.
	 */
	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		Mail mail = MailDAO.getInstance().getReceivedMailByMailId(activeChar.getObjectId(), postId);
		if (mail != null)
		{
			if (mail.isUnread())
			{
				mail.setUnread(false);
				mail.setJdbcState(JdbcEntityState.UPDATED);
				mail.update();
				activeChar.sendPacket(new ExChangePostState(true, Mail.READED, mail));
			}
			activeChar.sendPacket(new ExReplyReceivedPost(mail));
			return;
		}
		activeChar.sendPacket(new ExShowReceivedPostList(activeChar));
	}
}
