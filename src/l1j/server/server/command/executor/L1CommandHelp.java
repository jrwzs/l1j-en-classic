/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.command.executor;

import java.util.List;

import l1j.server.server.command.L1Commands;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_RawStringDialog;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Command;

public class L1CommandHelp implements L1CommandExecutor {

	private static int commandsPerPage = 40;
	
	private L1CommandHelp() {
	}
	
	public String getHelpText() {
		return "";
	}

	public static L1CommandExecutor getInstance() {
		return new L1CommandHelp();
	}

	private String join(List<L1Command> list, String with) {
		StringBuilder result = new StringBuilder();
		for (L1Command cmd : list) {
			if (result.length() > 0) {
				result.append(with);
			}
			result.append(cmd.getName());
		}
		return result.toString();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		List<L1Command> list = L1Commands.availableCommandList(pc
				.getAccessLevel());
		
		int pages = (int)Math.ceil((double)list.size() / commandsPerPage);
		
		try {
			if(arg.trim().equals("") || Integer.parseInt(arg) > pages)
				throw new Exception();
			
			int page = Integer.parseInt(arg);
			int end = page * commandsPerPage;
			
			if(end > list.size())
				end = list.size();
			
			List<L1Command> pageList = list.subList((page - 1) * commandsPerPage , end);
			
			pc.sendPackets(new S_RawStringDialog(pc.getId(), 
					String.format("Available GM Commands (Page %d)", page), join(pageList, ", ")));
		} catch(Exception ex) {
			pc.sendPackets(new S_SystemMessage(String.format(
					".help [page #] -- Currently %d pages.", pages)));
		}
	}
}
