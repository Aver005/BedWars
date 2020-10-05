package kiviuly.bedwars;

import java.io.File;
import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Commands implements CommandExecutor
{
	private Main main;
	private String PREFIX = "§6§l[BedWars] ";
	public Commands(Main main) {this.main = main;}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args) 
	{
		if (sender instanceof Player)
		{
			Player p = (Player) sender;
			if (args.length == 0) {main.OpenGameMenu(p); return true;}
			String sub = args[0].toLowerCase();
			
			if (sub.equals("help")) 
			{
				SendHelpMessage(p); 
				return true;
			}
			
			if (sub.equals("join"))
			{
				if (args.length == 1) {main.OpenGameMenu(p); return true;}
				String name = args[1].toUpperCase();
				Arena arena = main.getArena(name);
				if (arena == null) {SMP("§cОшибка. Данная арена не существует.", p); return true;} 
				arena.addPlayerToLobby(p);
				SMP(p, "§2Тут что-то написано");
				return true;
			}
			
			if (sub.equals("leave")) 
			{
				return true;
			}
			
			if (sub.equals("stats")) 
			{
				return true;
			}
			
			if (sub.equals("info")) 
			{
				return true;
			}
			
			if (!p.hasPermission("bedwars.admin")) {SMP(p,"§cНеизвестная подкоманда."); return true;}
			
			if (sub.equals("review")) 
			{
				String msg =
				  "§7╔═════ §2Создание и настройка арены §7═════►" + "\n"
				+ "§7‖" + "\n"
				+ "§7‖  §2Для создания арены: §e/bw createarena <Название> <Минимальное количество игроков>" + "\n"
				+ "§7‖  §cНАЗВАНИЕ НЕ ДОЛЖНО СОДЕРЖАТЬ ПРОБЕЛЫ И СИМВОЛЫ" + "\n"
				+ "§7‖  §cМИНИМАЛЬНОЕ КОЛИЧЕСТВО ИГРОКОВ ДОЛЖНО БЫТЬ ЧИСЛОМ" + "\n"
				+ "§7‖  §2После создания арены, вы получили 2 блока." + "\n"
				+ "§7‖  §bАлмазный §2блок ставьте там, где будут появляться §bалмазы§2." + "\n"
				+ "§7‖  §aИзумрудный §2блок ставьте там, где будут появляться §aизумруды§2." + "\n"
		        + "§7‖" + "\n"
		        + "§7‖  §2Теперь добавьте команды: §e/bw addteam <Арена> <Название команды> <Игроков в команде>" + "\n"
		        + "§7‖  §cНАЗВАНИЕ КОМАНДЫ НЕ ДОЛЖНО СОДЕРЖАТЬ ПРОБЕЛЫ И СИМВОЛЫ" + "\n"
				+ "§7‖  §cКОЛИЧЕСТВО ИГРОКОВ В КОМАНДЕ ДОЛЖНО БЫТЬ ЧИСЛОМ" + "\n"
				+ "§7‖  §2После создания арены, вы получили 5 блоков." + "\n"
				+ "§7‖  §7Железный §2блок ставьте, где §lУ ЭТОЙ КОМАНДЫ §2будет появляться §7железо§2 (Один раз)." + "\n"
				+ "§7‖  §6Золотой §2блок ставьте, где §lУ ЭТОЙ КОМАНДЫ §2будет появляться §6золото§2 (Один раз)." + "\n"
				+ "§7‖  §aИзумрудный §2блок ставьте, где §lУ ЭТОЙ КОМАНДЫ §2будут появляться §aизумруды§2 (Один раз)." + "\n"
				+ "§7‖  §3Маяк §2ставьте, где будет появляться §lЭТА КОМАНДА §2(Один раз)." + "\n"
				+ "§7‖  §cКро§fвать §2ставьте, где будет появляться §cкро§fвать §2§lЭТОЙ КОМАНДЫ §2(Один раз)." + "\n"
				+ "§7‖  §2§lПроделайте этот алгоритм для всех команд." + "\n"
				+ "§7‖" + "\n"
				+ "§7‖  §2Теперь создайте магазин: §e/bw createshop <Название магазина>" + "\n"
				+ "§7‖  §cНАЗВАНИЕ НЕ ДОЛЖНО СОДЕРЖАТЬ ПРОБЕЛЫ И СИМВОЛЫ" + "\n"
				+ "§7‖  §cНАЗВАНИЕ НЕ ДОЛЖНО СОВПАДАТЬ С НАЗВАНИЯМИ АРЕН" + "\n"
				+ "§7‖  §2Учтите, что один и тот же магазин можно использовать" + "\n"
				+ "§7‖  §2На разных аренах одновременно!" + "\n"
				+ "§7‖  §2После удачного создания магазина, в чате появится название файла" + "\n"
				+ "§7‖  §2В нём вы по примеру настраивайте магазин (Ссылка на пример указана в файле)." + "\n"
				+ "§7‖  §2В конце, добавьте его на арену: §e/bw addshop <Название арены> <Название магазина>" + "\n"
				+ "§7‖  §2Получите отметчик: §e/bw getshop <Название арены> <Название магазина>" + "\n"
				+ "§7‖  §2Нажимая ПКМ по блоку, вы добавляете точку появления этого магазина на арене." + "\n"
				+ "§7‖" + "\n"
				+ "§7‖  §2Теперь проверьте, что в косоли и чате нет ошибок. И пишите §e/bw check <Название арены>" + "\n"
				+ "§7‖  §2Если вы всё сделали правильно, то вам напишет:" + "\n"
				+ "§7‖  '§b§lПоздравляю с успешным созданием и настройкой арены §a§l<Название арены>§b§l.'" + "\n"
				+ "§7‖  §2Осталось включить арену для игры: §e/bw enable <Название арены>" + "\n"
				+ "§7‖  §2§l:-)" + "\n"
		        + "§7‖" + "\n"
		        + "§7╚═══════════════════════════════►";
				p.sendMessage(msg);
				return true;
			}
			
			if (args.length < 2) {SMP(p,"§cНеизвестная подкоманда или не все аргументы."); return true;}
			String name = args[1].toUpperCase();
			
			if (sub.equals("createarena") || sub.equals("carena") || sub.equals("ca"))
			{
				if (args.length < 3) {SMP(p,"§cНе все аргументы."); return true;}
				if (main.isArena(name) ) {SMP(p,"§cТакая арена существует."); return true;}
				if (main.isShop(name) ) {SMP(p,"§cТакой магазин существует (имя арены и магазина не должны совпадать)."); return true;}
				if (name.equals("ALL")) {SMP(p,"§cНазвание не допустимо!"); return true;}
				if (!main.isInt(args[2])) {SMP(p,"§cКоличество игроков должно быть числом."); return true;}
				File file = new File(main.getDataFolder()+File.separator+"Arenas");
				if (!file.exists()) {file.mkdir();}
				file = new File(main.getDataFolder()+File.separator+"Arenas"+File.separator+name+".yml");
				if (file.exists()) {SMP(p,"§cФайл данной арены существует. Удалите его (§e"+name+".yml§c)."); return true;}
				try 
				{
					file.createNewFile();
					int minPlCount = Integer.parseInt(args[2]);
					Arena arena = new Arena(name, name, minPlCount, p.getUniqueId());
					main.addArena(name, arena);
					boolean isSaved = main.saveArena(arena, file);
					if (isSaved) 
					{
						main.getServer().dispatchCommand(p, "bw getemerald "+name);
						main.getServer().dispatchCommand(p, "bw getdiamond "+name);
						SMP("§2Арена §a"+name+"§2 создана. Отметчики выданы вам в инвентарь.", p);
						return true;
					}
					
					SMP("§cОшибка сохранения арены в файл. Смотрите в консоль.", p);
					return true;
				} 
				catch (IOException e) 
				{
					SMP("§cОшибка создания файла арены. Смотри в консоль.", p);
					e.printStackTrace();
					return true;
				}
			}
			
			if (sub.equals("createshop") || sub.equals("cshop") || sub.equals("cs"))
			{
				if (main.isShop(name) ) {SMP(p,"§cТакой магазин существует."); return true;}
				if (main.isArena(name) ) {SMP(p,"§cТакая арена существует (имя магазина и арены не должны совпадать)."); return true;}
				if (name.equals("ALL")) {SMP(p,"§cНазвание не допустимо!"); return true;}
				File file = new File(main.getDataFolder()+File.separator+"Shops");
				if (!file.exists()) {file.mkdir();}
				file = new File(main.getDataFolder()+File.separator+"Shops"+File.separator+name+".yml");
				if (file.exists()) {SMP(p,"§cФайл данного магазина существует. Удалите его (§e"+name+".yml§c)."); return true;}
				try 
				{
					file.createNewFile();
					Shop shop = new Shop(name, p.getUniqueId());
					main.addShop(name, shop);
					boolean isSaved = main.saveShop(shop, file);
					if (isSaved) 
					{
						SMP("§2Магазин §a"+name+"§2 создан. Настройте его в файле §e"+file.getPath(), p);
						return true;
					}
					
					SMP("§cОшибка сохранения магазина в файл. Смотрите в консоль.", p);
					return true;
				} 
				catch (IOException e) 
				{
					SMP("§cОшибка создания файла магазина. Смотри в консоль.", p);
					e.printStackTrace();
					return true;
				}
			}
			
			if (!main.isArena(name) && !main.isShop(name)) {SMP(p,"§cТакой арены или магазина не существует."); return true;}
			
			if (sub.equals("setlobby"))
			{
				Arena arena = main.getArena(name);
				if (arena == null) {SMP("§cОшибка. Данная арена не существует.", p); return true;} 
				arena.setLobbyLocation(p.getLocation());
				SMP(p, "§2Тут что-то написано");
				return true;
			}
			
			if (sub.equals("setspectspawn"))
			{
				Arena arena = main.getArena(name);
				if (arena == null) {SMP("§cОшибка. Данная арена не существует.", p); return true;} 
				arena.setSpectatorsSpawn(p.getLocation());
				SMP(p, "§2Тут что-то написано");
				return true;
			}
			
			if (sub.equals("enable") || sub.equals("on"))
			{
				Arena arena = main.getArena(name);
				if (arena == null) {SMP("§cОшибка. Данная арена не существует.", p); return true;} 
				arena.setEnabled(true);
				SMP(p, "§2Арена §b"+arena.getID()+"§2 теперь доступна для игры.");
				return true;
			}
			
			if (sub.equals("disable") || sub.equals("off"))
			{
				Arena arena = main.getArena(name);
				if (arena == null) {SMP("§cОшибка. Данная арена не существует.", p); return true;} 
				arena.setEnabled(false);
				SMP(p, "§eАрена §b"+arena.getID()+"§e теперь НЕ доступна для игры.");
				return true;
			}
			
			if (sub.equals("stop"))
			{
				Arena arena = main.getArena(name);
				if (arena == null) {SMP("§cОшибка. Данная арена не существует.", p); return true;} 
				arena.Stop(5);
				SMP(p, "§eАрена остановлена.");
				return true;
			}
			
			if (sub.equals("start"))
			{
				Arena arena = main.getArena(name);
				if (arena == null) {SMP("§cОшибка. Данная арена не существует.", p); return true;} 
				arena.Start(15);
				SMP(p, "§2Игра скоро начнётся.");
				return true;
			}
			
			if (sub.equals("testshop"))
			{
				Shop shop = main.getShop(name);
				if (shop == null) {SMP("§cОшибка. Данный магазин не существует.", p); return true;}
				shop.Open(p, "MainPage");
				SMP("§2Инвентарь закупки магазина §e"+shop.getID()+"§2 открыт.", p); 
				return true;
			}
			
			if (sub.equals("savearena"))
			{
				File file = new File(main.getDataFolder()+File.separator+"Arenas");
				if (!file.exists()) {file.mkdir();}
				file = new File(main.getDataFolder()+File.separator+"Arenas"+File.separator+name+".yml");
				if (!file.exists()) {try {file.createNewFile();} catch (IOException e) {e.printStackTrace();}}
				Arena arena = main.getArena(name);
				if (arena == null) {SMP("§cОшибка. Арены не существует.", p); return true;}
				boolean isSaved = main.saveArena(arena, file);
				if (isSaved) 
				{
					SMP("§2Арена §a"+name+"§2 сохранена в файл: §e"+file.getPath(), p);
					return true;
				}
				
				SMP("§cОшибка сохранения арены в файл. Смотрите в консоль.", p);
				return true;
			}
			
			if (sub.equals("loadarena"))
			{
				File file = new File(main.getDataFolder()+File.separator+"Arenas");
				if (!file.exists()) {file.mkdir();}
				file = new File(main.getDataFolder()+File.separator+"Arenas"+File.separator+name+".yml");
				if (!file.exists()) {SMP("§cОшибка. Файл арены не существует.", p); return true;}
				Arena arena = main.getArena(name);
				if (arena == null) {SMP("§cОшибка. Арены не существует.", p); return true;}
				boolean isLoaded = main.loadArena(arena, file);
				if (isLoaded) 
				{
					SMP("§2Арена §a"+name+"§2 загружена из файла: §e"+file.getPath(), p);
					return true;
				}
				
				SMP("§cОшибка загрузки арены из файла. Смотрите в консоль.", p);
				return true;
			}
			
			if (sub.equals("saveshop"))
			{
				File file = new File(main.getDataFolder()+File.separator+"Shops");
				if (!file.exists()) {file.mkdir();}
				file = new File(main.getDataFolder()+File.separator+"Shops"+File.separator+name+".yml");
				if (!file.exists()) {try {file.createNewFile();} catch (IOException e) {e.printStackTrace();}}
				Shop shop = main.getShop(name);
				if (shop == null) {SMP("§cОшибка. Магазина не существует.", p); return true;}
				boolean isSaved = main.saveShop(shop, file);
				if (isSaved) 
				{
					SMP("§2Магазин §a"+name+"§2 сохранен в файл: §e"+file.getPath(), p);
					return true;
				}
				
				SMP("§cОшибка сохранения магазина в файл. Смотрите в консоль.", p);
				return true;
			}
			
			if (sub.equals("loadshop"))
			{
				File file = new File(main.getDataFolder()+File.separator+"Shops");
				if (!file.exists()) {file.mkdir();}
				file = new File(main.getDataFolder()+File.separator+"Shops"+File.separator+name+".yml");
				if (!file.exists()) {SMP("§cОшибка. Файл магазина не существует.", p); return true;}
				Shop shop = main.getShop(name);
				if (shop == null) {SMP("§cОшибка. Магазина не существует.", p); return true;}
				boolean isLoaded = main.loadShop(shop, file);
				if (isLoaded) 
				{
					SMP("§2Магазин §a"+name+"§2 загружен из файла: §e"+file.getPath(), p);
					return true;
				}
				
				SMP("§cОшибка загрузки магазина из файла. Смотрите в консоль.", p);
				return true;
			}
			
			if (sub.startsWith("get"))
			{
				ItemStack is = null;
				Material mat = Material.EMERALD_BLOCK;
				String lore0 = "§a§lИЗУМРУДНЫХ §fгенераторов";
				String lore2 = "§a§lИЗУМРУДЫ";
				String team = "";
				String shop = "";
				Arena arena = main.getArena(name);
				if (arena == null) {SMP(p, "§cАрена не найдена."); return true;}
				
				if (sub.startsWith("getteam"))
				{
					if (args.length < 3) {SMP(p, "§cВы не указали имя команды."); return true;}
					team = args[2].toUpperCase();
					if (!arena.isTeamExist(team)) {SMP(p, "§cДанная команда не существует."); return true;}
				}
				
				switch(sub)
				{
					case "getemerald":
						mat = Material.EMERALD_BLOCK;
						lore0 = "§a§lИЗУМРУДНЫХ §fгенераторов";
						lore2 = "§a§lИЗУМРУДЫ";
					break;
					
					case "getdiamond":
						mat = Material.DIAMOND_BLOCK;
						lore0 = "§b§lАМАЗНЫХ §fгенераторов";
						lore2 = "§b§lАЛМАЗЫ";
					break;
					
					case "getteamiron":
						mat = Material.IRON_BLOCK;
						lore0 = "§7§lЖЕЛЕЗНЫХ §fгенераторов команды §e"+team;
						lore2 = "§7§lЖЕЛЕЗО §fкоманды §e"+team;
					break;
					
					case "getteamgold":
						mat = Material.GOLD_BLOCK;
						lore0 = "§6§lЗОЛОТЫХ  §fгенераторов команды §e"+team;
						lore2 = "§6§lЗОЛОТО §fкоманды §e"+team;
					break;
					
					case "getteamemerald":
						mat = Material.EMERALD_BLOCK;
						lore0 = "§a§lИЗУМРУДНЫХ  §fгенераторов команды §e"+team;
						lore2 = "§a§lИЗУМРУДЫ §fкоманды §e"+team;
					break;
					
					case "getteambed":
						mat = Material.BED;
						lore0 = "§cкро§fвати §2команды §e"+team;
						lore2 = "§cкро§fвать §fкоманды §e"+team;;
					break;
					
					case "getteamspawn":
						mat = Material.BEACON;
						lore0 = "§3спавна §fкоманды §e"+team;
						lore2 = "§3игроки §fкоманды §e"+team;
					break;
					
					case "getshop":
						shop = args[2].toUpperCase();
						mat = Material.MONSTER_EGG;
						lore0 = "§3спавна §fторговца §e"+shop;
						lore2 = "§3торговец §e"+shop;
					break;
				}
				
				is = new ItemBuilder(mat)
					.displayname("§aОтметчик")
					.lore("§fОтметчик "+lore0)
					.lore("§fПоставьте, где будут спавниться")
					.lore(lore2)
					.lore("")
					.lore("§fАрена: §e"+name)
					.lore("§fКоманда: §e"+team)
					.lore("§fТорговец: §e"+shop)
					.build();
				p.getInventory().addItem(is);
				SMP(p, "§2Отметчик "+lore0+" §2получен.");
				return true;
			}
			
			if (args.length < 3) {SMP(p,"§cНеизвестная подкоманда или не все аргументы."); return true;}
			
			
			
			if (args.length < 4) {SMP(p,"§cНеизвестная подкоманда или не все аргументы."); return true;}
			
			if (sub.equals("addbuyitem"))
			{
				Shop shop = main.getShop(name);
				if (shop == null) {SMP("§cОшибка. Арены не существует.", p); return true;}
				String category = args[2];
				String price = args[3].toUpperCase();
				ItemStack is = p.getItemInHand();
				if (is == null) {SMP("§cОшибка. Вы не держите предмет в руках.", p); return true;}
				shop.addSlotItem(category, is, price);
				return true;
			}
			
			if (sub.equals("addteam"))
			{
				if (!main.isInt(args[3])) {SMP("§cКоличество игроков - число.", p); return true;}
				Arena arena = main.getArena(name);
				if (arena == null) {SMP("§cОшибка. Арены не существует.", p); return true;}
				String teamName = args[2].toUpperCase();
				if (arena.isTeamExist(teamName)) {SMP("§cТакая команда уже существует.", p); return true;}
				Integer maxPlCount = Integer.parseInt(args[3]);
				int result = arena.addTeam(teamName, maxPlCount, "&f&l");
				switch(result)
				{
					case 0: 
						main.getServer().dispatchCommand(p, "bw getteamiron "+name+" "+teamName);
						main.getServer().dispatchCommand(p, "bw getteamgold "+name+" "+teamName);
						main.getServer().dispatchCommand(p, "bw getteamemerald "+name+" "+teamName);
						main.getServer().dispatchCommand(p, "bw getteambed "+name+" "+teamName);
						main.getServer().dispatchCommand(p, "bw getteamspawn "+name+" "+teamName);
						SMP("§2Команда §b"+teamName+" §2создана. Установите ей цвет §e/bw setteamcolor "+name+" "+teamName+" <Цвет>", p); 
					return true;
					case 1: SMP("§cИмя команды не должно быть пустым.", p); return true;
					case 2: SMP("§cКоличество игроков должно быть от 1 до 16.", p); return true;
					case 3: SMP("§cЦвет неправильный (&1,&2,&3,&4,&5,&6,&7,&8,&9,&a,&b,&c,&d,&e).", p); return true;
				}
				return true;
			}
			
			if (sub.equals("setteamname"))
			{
				Arena arena = main.getArena(name);
				if (arena == null) {SMP("§cОшибка. Арены не существует.", p); return true;}
				String teamName = args[2].toUpperCase();
				if (!arena.isTeamExist(teamName)) {SMP("§cТакая команда НЕ существует.", p); return true;}
				String dName = args[3];
				Team team = arena.getTeamByID(teamName);
				team.setName(dName);
				SMP("§2Имя команды "+team.getColor()+teamName+" §2теперь "+dName+"§2.", p);
				return true;
			}
			
			if (sub.equals("setteamcolor"))
			{
				Arena arena = main.getArena(name);
				if (arena == null) {SMP("§cОшибка. Арены не существует.", p); return true;}
				String teamName = args[2].toUpperCase();
				if (!arena.isTeamExist(teamName)) {SMP("§cТакая команда НЕ существует.", p); return true;}
				String color = args[3].toLowerCase();
				if (!color.contains("&")) {SMP("§cЦвет неправильный (&1,&2,&3,&4,&5,&6,&7,&8,&9,&a,&b,&c,&d,&e).", p); return true;}
				Team team = arena.getTeamByID(teamName);
				team.setColor(color);
				SMP("§2Цвет для команды "+color+teamName+" §2устновлен.", p);
				return true;
			}
		}
		
		return false;
	}
	
	public void SendHelpMessage(Player p) 
	{
		String msg = 
	          "§7╔═════ §2Команды мини-режима §7═════►" + "\n"
	        + "§7‖" + "\n"
	        + "§7‖  §b/bw §7——— §2Открыть меню мини-игры" + "\n"
	        + "§7‖  §b/bw join §7——— §2Вступить в случайный матч" + "\n"
	        + "§7‖  §b/bw join [Арена] §7——— §2Вступить в открытый матч" + "\n"
	        + "§7‖  §b/bw leave §7——— §2Выйти из матча" + "\n"
	        + "§7‖  §b/bw stats §7——— §2Посмотреть статистику" + "\n"
	        + "§7‖  §b/bw stats [Игрок] §7——— §2Посмотреть статистику игрока" + "\n"
	        + "§7‖  §b/bw info §7——— §2Описание и правила режима" + "\n"
	        + "§7‖" + "\n"
	        + "§7╚═══════════════════════════════►";
	
	    if (p.hasPermission("bedwars.admin"))
	    {
	        msg +=
	          "\n\n"
	        + "§7╔═════ §eКоманды админа §7═════►" + "\n"
	        + "§7‖" + "\n"
	        + "§7‖  §c/bw reload §7——— §eПерезагрузить настройки плагина" + "\n"
	        + "§7‖  §c/bw save §7——— §eСохранить настройки плагина" + "\n"
	        + "§7‖  §c/bw list §7——— §eСписок арен" + "\n"
	        + "§7‖  §c/bw stop <ArenaID/all> §7——— §eОстановить игру" + "\n"
	        + "§7‖  §c/bw start <ArenaID/all> §7——— §eЗапустить игру" + "\n"
	        + "§7‖" + "\n"
	        + "§7‖  §c/bw createarena <ArenaID> <Число мин. игроков> §7——— §eСоздать новую арену" + "\n"
	        + "§7‖  §c/bw setarenaname <ArenaID> <Name> §7——— §eВыставить название арены" + "\n"
	        + "§7‖  §c/bw setarenadesc <ArenaID> <Description> §7——— §eВыставить описание арены" + "\n"
	        + "§7‖  §c/bw addteam <ArenaID> <TeamID> <Players> §7——— §eСоздать команду" + "\n"
	        + "§7‖  §c/bw setteamname <ArenaID> <TeamID> <Name> §7——— §eВыставить название команды" + "\n"
	        + "§7‖  §c/bw setteamcolor <ArenaID> <TeamID> <Color> §7——— §eВыставить цвет команды" + "\n"
	        + "§7‖  §c/bw addblocks <ArenaID> <BlockIDs> §7——— §eДобавить блоки (которые можно ставить)" + "\n"
	        + "§7‖  §c/bw setblocks <ArenaID> <BlockIDs> §7——— §eУстановить блоки (которые можно ставить)" + "\n"
	        + "§7‖  §c/bw removeblocks <ArenaID> <BlockIDs> §7——— §eУбрать блоки (которые можно ставить)" + "\n"
	        + "§7‖  §c/bw getemerald <ArenaID> §7——— §eПолучить отметчик изумрудных генераторов" + "\n"
	        + "§7‖  §c/bw getdiamond <ArenaID> §7——— §eПолучить отметчик алмазных генераторов" + "\n"
	        + "§7‖  §c/bw getteamiron <ArenaID> <TeamName> §7——— §eПолучить отметчик железных ген." + "\n"
	        + "§7‖  §c/bw getteamgold <ArenaID> <TeamName> §7——— §eПолучить отметчик золотых ген." + "\n"
	        + "§7‖  §c/bw getteamemerald <ArenaID> <TeamName> §7——— §eПолучить отметчик изумрудных ген." + "\n"
	        + "§7‖  §c/bw getteamspawn <ArenaID> <TeamName> §7——— §eПолучить отметчик точек спавна." + "\n"
	        + "§7‖  §c/bw getteambed <ArenaID> <TeamName> §7——— §eПолучить отметчик кровати." + "\n"
	        + "§7‖" + "\n"
	        + "§7‖  §c/bw createshop <ShopID> §7——— §eСоздать магазин (настройка в конфиге)." + "\n"
	        + "§7‖  §c/bw addshop <ArenaID> <ShopID> §7——— §eДобавить магазин на арену." + "\n"
	        + "§7‖  §c/bw getshop <ArenaID> <ShopID> §7——— §eПолучить отметчик спавна магазина." + "\n"
	        + "§7‖" + "\n"
	        + "§7‖  §eДля получения инструкции по созданию и настройке арены," + "\n"
	        + "§7‖  §eНапишите команду §c/bw review" + "\n"
	        + "§7‖" + "\n"
	        + "§7‖  §c/bw enable <ArenaID> §7——— §eВключить арену" + "\n"
	        + "§7‖  §c/bw disable <ArenaID> §7——— §eВыключить арену" + "\n"
	        + "§7‖  §c/bw check <ArenaID> §7——— §eПроверить арену на настройку" + "\n"
	        + "§7‖  §c/bw review §7——— §eПолучить инструкцию" + "\n"
	        + "§7‖" + "\n"
	        + "§7╚═══════════════════════════════►";
	    }
	
	    p.sendMessage(msg);
	}
	
	public void SM(Player p, String msg) {p.sendMessage(msg);}
	public void SM(String msg, Player p) {p.sendMessage(msg);}
	public void SMP(String msg, Player p) {p.sendMessage(PREFIX+msg);}
	public void SMP(Player p, String msg) {p.sendMessage(PREFIX+msg);}
}