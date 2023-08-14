package com.umut.ssh.command;

import com.umut.ssh.filesystem.Directory;
import com.umut.ssh.filesystem.File;
import com.umut.ssh.service.JWTService;
import com.umut.ssh.service.RestService;
import com.umut.ssh.suspiciousactivity.Origin;
import com.umut.ssh.util.DataLog;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class DummyCommand implements Command {

    public void log(String msg) {
        System.out.println("Test SSHd: " + DummyCommand.class.getSimpleName() + ": " + channel.getSession().getRemoteAddress() + ": " + msg);
        DataLog logger = new DataLog();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String entryTime = dateFormat.format(new Date());
        char firstChar = channel.getSession().getRemoteAddress().toString().charAt(0);
        String ip = channel.getSession().getRemoteAddress().toString().replaceFirst(Character.toString(firstChar), "");
        logger.LogToFileDummyCommand("Session IP: " + ip + ": " + msg);
        if (Objects.equals(msg, "start()")) {
            logger.SaveLogEntriesToDatabase(msg, ip, entryTime);
            getRestService().postSuspiciousActivity(new Origin(ip,null),entryTime,msg);
        }
    }

    public static String PROMPT = "$ ";

    private ChannelSession channel;
    private InputStream in;
    private OutputStream out, err;
    private ExitCallback callback;
    private final StringBuffer commandLine = new StringBuffer();
    private Directory localDir = Directory.createRootDirectory();

    private final String username;


    private static RestService getRestService() {
        RestTemplateBuilder rest = new RestTemplateBuilder();
        return new RestService(rest, getJwtService());
    }

    private static JWTService getJwtService() {
        return new JWTService();
    }


    public DummyCommand(ChannelSession channel) {
        this.channel = channel;

        log("DummyCommand()");
        username = channel.getSessionContext().getUsername();
        try {
            Directory etc = localDir.createDirectory("etc");
            etc.createFile("passwd");
            etc.createFile("hosts");
            etc.createFile("imhosts.sam");
            etc.createFile("networks");
            etc.createFile("protocol");
            etc.createFile("services");
            localDir.createDirectory("bin");
            localDir.createDirectory("lib");
            Directory usr = localDir.createDirectory("usr");
            usr.createDirectory("bin");
            usr.createDirectory("lib");
            localDir.createDirectory("var");
            localDir.createDirectory("root");
            Directory home = localDir.createDirectory("user");
            home.createDirectory("appdata");
            home.createDirectory("roaming");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void run() {
        try {
            while (in != null) {
                int c = in.read();
                if (c > 0) {
                    if (c == 0x08 || c == 0x7F) {
                        if (!commandLine.isEmpty()) {
                            out.write(c);
                            out.flush();
                            commandLine.deleteCharAt(commandLine.length() - 1);
                        }
                    } else {
                        out.write(c);
                        out.flush();
                        commandLine.append((char) c);
                        if (c == '\r') {
                            out.write('\n');
                            out.flush();
                            String command = commandLine.toString().trim();
                            commandLine.delete(0, commandLine.length());
                            handleCommand(command);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleCommand(String command) {
        log("command: " + command);
        if (command.isEmpty()) {
            printPrompt();
        } else if (command.equals("exit")) {
            if (callback != null) callback.onExit(0, "Requested to exit");
        } else if (command.startsWith("netstat")) {
            String netstatOutput =
                    """     
                            Active Internet connections (servers and established)
                            Proto Recv-Q Send-Q Local Address           Foreign Address           State       \r
                            tcp        0      0 0.0.0.0:22              0.0.0.0:*                 LISTEN      \r
                            tcp6       0      0 :::22                   :::*                      LISTEN      \r
                            udp        0      0 0.0.0.0:68              0.0.0.0:*                 ESTABLISHED \r
                            udp        0      0 0.0.0.0:123             0.0.0.0:*                 TIME_WAIT   \r
                            udp6       0      0 :::123                  :::*                      TIME_WAIT   \r
                            tcp        0      0 172.16.186.1:55306      ec2-52-74-196-144:https   ESTABLISHED \r
                            tcp        0      0 172.16.186.1:53414      132.101.192.134:https     ESTABLISHED \r
                            tcp        0      0 172.16.186.1:54367      bom05s05-in-f5.1e:https   TIME_WAIT   \r
                            tcp        0      0 172.16.186.1:53611      maa03s23-in-f14.1:https   TIME_WAIT   \r
                            tcp        0      0 172.16.186.1:53567      ec2-34-208-200-70.:http   ESTABLISHED \r
                            tcp        0      0 172.16.186.1:52366      192.30.255.113:https      TIME_WAIT   \r
                            tcp        0      0 172.16.186.1:64362      151.101.192.134:https     ESTABLISHED \r
                            tcp        0      0 172.16.186.1:53125      maa03s23-in-f10.1e:http   TIME_WAIT   \r
                            tcp        0      0 172.16.186.1:53235      maa03s23-in-f10.1e:http   TIME_WAIT   \r
                            tcp        0      0 172.16.186.1:57823      maa03s23-in-f10.1e:http   TIME_WAIT   \r
                            tcp        0      0 172.16.186.1:55216      mullberry.canonical:https TIME_WAIT   \r

                            """;
            printOut(netstatOutput);
            printPrompt();
        } else if (command.equals("ls")) {
            StringBuffer sb = new StringBuffer();
            for (Directory d : localDir.getDirectories()) sb.append(d).append(' ');
            for (File f : localDir.getFiles()) sb.append(f).append(' ');
            String commandOutput = sb.toString();
            log("output: " + commandOutput);
            printOut(commandOutput + "\r\n");
            printPrompt();
        } else if (command.startsWith("cd")) {
            String[] params = command.split(" ");
            if (params.length > 1) {
                String name = params[1].trim();
                if (name.equals("..")) {
                    if (!localDir.isRoot()) localDir = localDir.getParentDirectory();
                } else {
                    Directory newDir = localDir.getDirectory(name);
                    if (newDir != null) localDir = newDir;
                    else printOut("Directory '" + name + "' not found\r\n");
                }
            }
            printPrompt();
        } else if (command.equals("clear")) {
            printOut("\033[H\033[2J");
            log("output: page cleared");
            printPrompt();
        } else if (command.startsWith("apt")) {
            String[] params = command.split(" ");
            if (params.length >= 2) {
                String subcommand = params[1].trim();
                if (subcommand.equals("update")) {
                    // Simulate apt update
                    printOut("Updating package index... Done.\r\n");
                } else if (subcommand.equals("install")) {
                    if (params.length >= 3) {
                        String packageName = params[2].trim();
                        // Simulate apt install
                        printOut("Installing package " + packageName + " ...\r\n");
                        try {
                            Thread.sleep(1000);
                            printOut(packageName + " installed...\r\n");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        try {
                            localDir.createFile(packageName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        log("output: Usage: apt install PACKAGE");
                        printOut("Usage: apt install PACKAGE\r\n");
                    }
                } else {
                    log("output: Unknown subcommand: " + subcommand);
                    printOut("Unknown subcommand: " + subcommand + "\r\n");
                }
            } else {
                log("output: Usage: apt update|install");
                printOut("Usage: apt update|install\r\n");
            }
            printPrompt();
        } else if (command.startsWith("mkdir")) {
            String[] params = command.split(" ");
            if (params.length > 1) {
                String name = params[1].trim();
                if (name.equals("-h")) {
                    printOut("Usage: mkdir [OPTIONS]... DIRECTORY...\r\n");
                    printOut("Create the DIRECTORY, if they do not already exist.\r\n");
                    printOut("\t-m MODE Mode;\r\n");
                    printOut("\t-p no error if existing, make parent directories as needed;\r\n");
                    printOut("\t-v print a message for each created directory;\r\n");
                    printOut("\t-h display this help and exit;\r\n\n");
                    printOut("Full documentation <https://www.gnu.org/software/coreutils/mkdir>\r\n");
                    log("output: " + command + " description");
                } else if (name.equals("--help")) {
                    printOut("Usage: mkdir [OPTIONS]... DIRECTORY...\r\n");
                    printOut("Create the DIRECTORY, if they do not already exist.\r\n");
                    printOut("\t-m MODE Mode;\r\n");
                    printOut("\t-p no error if existing, make parent directories as needed;\r\n");
                    printOut("\t-v print a message for each created directory;\r\n");
                    printOut("\t--help display this help and exit;\r\n\n");
                    printOut("Full documentation <https://www.gnu.org/software/coreutils/mkdir>\r\n");
                    log("output: " + command + " description");
                } else {
                    try {
                        localDir.createDirectory(name);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    printOut("Directory '" + name + "' created \r\n");
                    log("output: Directory '" + name + "' created");
                }
            }
            printPrompt();
        } else if (command.startsWith("rm")) {
            String[] params = command.split(" ");
            if (params.length > 1) {
                String name = params[1].trim();
                if (name.equals("-h")) {
                    printOut("Usage: rm [OPTIONS]... DIRECTORY...\r\n");
                    printOut("Create the DIRECTORY, if they do not already exist.\r\n");
                    printOut("\t-p remove DIRECTORY and its ancestors;\r\n");
                    printOut("\t-v output a diagnostic for every directory processed;\r\n");
                    printOut("\t-h display this help and exit;\r\n\n");
                    printOut("Full documentation <https://www.gnu.org/software/coreutils/rmdir>\r\n");
                    log("output: " + command + " description");
                } else if (name.equals("--help")) {
                    printOut("Usage: rm [OPTIONS]... DIRECTORY...\r\n");
                    printOut("Create the DIRECTORY, if they do not already exist.\r\n");
                    printOut("\t-p remove DIRECTORY and its ancestors;\r\n");
                    printOut("\t-v output a diagnostic for every directory processed;\r\n");
                    printOut("\t--help display this help and exit;\r\n\n");
                    printOut("Full documentation <https://www.gnu.org/software/coreutils/rmdir>\r\n");
                    log("output: " + command + " description");
                } else {
                    try {
                        localDir.delDirectory(name);
                        localDir.delFile(name);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    printOut("Directory '" + name + "' removed \r\n");
                    log("output: Directory '" + name + "' removed ");
                }
            }
            printPrompt();
        } else if (command.equals("pwd")) {
            printOut(localDir.getPath() + "\r\n");
            log("output: " + localDir.getPath());
            printPrompt();
        } else if (command.startsWith("whoami")) {
            String[] params = command.split(" ");
            if (params.length > 1) {
                String name = params[1].trim();
                if (name.equals("-h")) {
                    printOut("Usage: whoami [OPTIONS]...\r\n");
                    printOut("Print the user name associated with the current effective user ID.\r\n");
                    printOut("\t-h display this help and exit;\r\n\n");
                    printOut("Full documentation <https://www.gnu.org/software/coreutils/whoami>\r\n");
                    log("output: " + command + " description");
                } else if (name.equals("--help")) {
                    printOut("Usage: whoami [OPTIONS]...\r\n");
                    printOut("Print the user name associated with the current effective user ID.\r\n");
                    printOut("\t-h display this help and exit;\r\n\n");
                    printOut("Full documentation <https://www.gnu.org/software/coreutils/whoami>\r\n");
                    log("output: " + command + " description");
                }
            } else {
                printOut(channel.getSession().getUsername() + "\r\n");
                log("output: " + channel.getSession().getUsername());
            }
            printPrompt();
        } else if (command.startsWith("echo")) {
            String[] params = command.split("echo ");
            String text = params[1].trim();
            printOut(text + "\r\n");
            log("output: " + text);
            printPrompt();
        } else if (command.startsWith("passwd") || command.startsWith("iptables") ||
                command.startsWith("grep") || command.startsWith("sudo") || command.startsWith("halt")) {
            printOut("Permission denied! You can't use the command " + command + "\r\n");
            log("output: " + command + " Permission denied");
            printPrompt();
        } else if (command.equals("help")) {
            printOut("\r\n\texit\tsudo\r\n");
            printOut("\thalt\tcat\r\n");
            printOut("\tls\tgrep\r\n");
            printOut("\tcd\tpasswd\r\n");
            printOut("\tclear\tiptables\r\n");
            printOut("\tmkdir\r\n");
            printOut("\trm\tnetstat\r\n");
            printOut("\tpwd\r\n");
            printOut("\twhoami\r\n");
            printOut("\techo\r\n\n");

            log("output: list of available command");
            printPrompt();
        } else {
            String commandOutput = command + ": command not found.";
            log("output: " + commandOutput);
            printOut(commandOutput + "\r\n");
            printPrompt();
        }

    }

    @Override
    public void start(ChannelSession channel, Environment env) throws IOException {
        log("start()");
        new Thread(this::run).start();
        this.channel = channel;
    }

    @Override
    public void destroy(ChannelSession channel) throws Exception {
        log("destroy()");
        in = null;
    }

    @Override
    public void setInputStream(InputStream in) {
        log("setInputStream()");
        this.in = in;
    }

    @Override
    public void setOutputStream(OutputStream out) {
        log("setOutputStream()");
        this.out = out;
        printOut("\r\nWelcome to Ubuntu 22.04.1 LTS (GNU/Linux 5.15.0-57-generic x86_64)\r\n");
        printOut(" * Documentation:  https://help.ubuntu.com\r\n");
        printOut(" * Management:     https://landscape.canonical.com\r\n");
        printOut(" * Support:        https://ubuntu.com/advantage\r\n");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = dateFormat.format(new Date());
        printOut("\r\n System information as " + currentTime + " UTC\r\n");
        printOut("\r\n  System load:           0.080078125\r\n");
        printOut("  Usage of /:            23.5% of 24.04GB\r\n");
        printOut("  Memory usage:          34%\r\n");
        printOut("  Swap usage:            6%\r\n");
        printOut("  Processes:             101\r\n");
        printOut("  Users logged in:       1\r\n");
        printOut("  IPv4 address for eth0: 172.104.249.194\r\n");
        printOut("  IPv6 address for eth0: 2a01:7e01::f03c:93ff:feca:a2fc\r\n");
        char firstChar = channel.getSession().getRemoteAddress().toString().charAt(0);
        printOut("\r\nLast login: " + currentTime + " from " + channel.getSession().getRemoteAddress().toString().replaceFirst(Character.toString(firstChar), "") + "\r\n");

        printPrompt();
    }

    private void printPrompt() {
        printOut(username + "@server:" + localDir.getPath() + PROMPT);
    }

    private void printOut(String str) {
        try {
            out.write(str.getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setErrorStream(OutputStream err) {
        log("setErrorStream()");
        this.err = err;
    }

    @Override
    public void setExitCallback(ExitCallback callback) {
        log("setExitCallback()");
        this.callback = callback;
    }

}
