package com.umut.ssh.command;

import com.umut.ssh.util.SimpleLog;
import jakarta.annotation.PostConstruct;
import org.apache.sshd.cli.CliLogger;
import org.apache.sshd.common.NamedResource;
import org.apache.sshd.common.PropertyResolver;
import org.apache.sshd.common.PropertyResolverUtils;
import org.apache.sshd.common.config.ConfigFileReaderSupport;
import org.apache.sshd.common.keyprovider.FileHostKeyCertificateProvider;
import org.apache.sshd.common.keyprovider.HostKeyCertificateProvider;
import org.apache.sshd.common.keyprovider.KeyPairProvider;
import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.scp.server.ScpCommandFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.pubkey.AcceptAllPublickeyAuthenticator;
import org.apache.sshd.server.config.SshServerConfigFileReader;
import org.apache.sshd.server.config.keys.ServerIdentity;
import org.apache.sshd.server.keyprovider.AbstractGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.ShellFactory;
import org.apache.sshd.server.subsystem.SubsystemFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static org.apache.sshd.cli.CliSupport.setupIoServiceFactory;
import static org.apache.sshd.cli.server.SshServerCliSupport.*;

@Component
public class SshServerMain {
    private SshServer sshdInstance;
    static String[] rootPasswords= {"123456", "root", "admin", "123", "0", "1"};
    static String[] piPasswords= {"raspberry", "pi"};

    @Value("${ssh.port}")
    private int port;

    @PostConstruct
    public void init(){
        Thread thread = new Thread(() -> {
            try {
                sshServerStart();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public void sshServerStart() throws Exception {
        boolean error = false;
        String hostKeyType = AbstractGeneratorHostKeyProvider.DEFAULT_ALGORITHM;
        int hostKeySize = 0;
        Collection<String> keyFiles = null;
        Collection<String> certFiles = null;
        Map<String, Object> options = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);


        String[] args = new String[0];
        int numArgs = GenericUtils.length(args);
        for (int i = 0; i < numArgs; i++) {
            String argName = args[i];
            if ("-p".equals(argName)) {
                i++;
                if (i >= numArgs) {
                    System.err.println("option requires an argument: " + argName);
                    error = true;
                    break;
                }
                port = Integer.parseInt(args[i]);
            } else if ("-key-type".equals(argName)) {
                i++;
                if (i >= numArgs) {
                    System.err.println("option requires an argument: " + argName);
                    error = true;
                    break;
                }

                if (keyFiles != null) {
                    System.err.println("option conflicts with -key-file: " + argName);
                    error = true;
                    break;
                }
                hostKeyType = args[i].toUpperCase();
            } else if ("-key-size".equals(argName)) {
                i++;
                if (i >= numArgs) {
                    System.err.println("option requires an argument: " + argName);
                    error = true;
                    break;
                }

                if (keyFiles != null) {
                    System.err.println("option conflicts with -key-file: " + argName);
                    error = true;
                    break;
                }

                hostKeySize = Integer.parseInt(args[i]);
            } else if ("-key-file".equals(argName)) {
                i++;
                if (i >= numArgs) {
                    System.err.println("option requires an argument: " + argName);
                    error = true;
                    break;
                }

                String keyFilePath = args[i];
                if (keyFiles == null) {
                    keyFiles = new LinkedList<>();
                }
                keyFiles.add(keyFilePath);
            } else if ("-o".equals(argName)) {
                i++;
                if (i >= numArgs) {
                    System.err.println("option requires and argument: " + argName);
                    error = true;
                    break;
                }

                String opt = args[i];
                int idx = opt.indexOf('=');
                if (idx <= 0) {
                    System.err.println("bad syntax for option: " + opt);
                    error = true;
                    break;
                }

                String optName = opt.substring(0, idx);
                String optValue = opt.substring(idx + 1);
                switch (optName) {
                    case ServerIdentity.HOST_KEY_CONFIG_PROP -> {
                        if (keyFiles == null) {
                            keyFiles = new LinkedList<>();
                        }
                        keyFiles.add(optValue);
                    }
                    case ServerIdentity.HOST_CERT_CONFIG_PROP -> {
                        if (certFiles == null) {
                            certFiles = new LinkedList<>();
                        }
                        certFiles.add(optValue);
                    }
                    case ConfigFileReaderSupport.PORT_CONFIG_PROP -> port = Integer.parseInt(optValue);
                    default -> options.put(optName, optValue);
                }
            }
        }

        PropertyResolver resolver = PropertyResolverUtils.toPropertyResolver(options);
        Level level = CliLogger.resolveLoggingVerbosity(resolver, args);
        Logger logger = CliLogger.resolveSystemLogger(SshServerMain.class, level);
        SshServer sshd = setupIoServiceFactory(
        SshServer.setUpDefaultServer(), resolver,
        level, System.out, System.err, args);
        if (sshd == null) {
            error = true;
        }

        if (error) {
            System.err.println(
                    "usage: sshd [-p port] [-io mina|nio2|netty] [-key-type RSA|DSA|EC] [-key-size ] [-key-file <path>] [-o option=value]");
            System.exit(-1);
            return;}

        Map<String, Object> props = sshd.getProperties();
        props.putAll(options);

        SshServerConfigFileReader.configure(sshd, resolver, true, true);
        KeyPairProvider hostKeyProvider = resolveServerKeys(System.err, hostKeyType, hostKeySize, null);
        sshd.setKeyPairProvider(hostKeyProvider);
        if (GenericUtils.isNotEmpty((Collection<?>) null)) {
            assert false;
            HostKeyCertificateProvider certProvider = new FileHostKeyCertificateProvider(
                    certFiles.stream().map(Paths::get).collect(Collectors.toList()));
            sshd.setHostKeyCertificateProvider(certProvider);
        }
        setupServerBanner(sshd, resolver);
        sshd.setPort(port);

        ShellFactory shellFactory = DummyCommand::new;
        SimpleLog.log("SSHd: SshServerMain: main(): shellFactory: " + shellFactory.getClass());
        if (logger.isInfoEnabled()) {
            logger.info("Using shell={}", shellFactory.getClass().getName());
        }
        sshd.setShellFactory(shellFactory);

        sshd.setPasswordAuthenticator((username, password, session) -> passwdCheck(session, username, password));
        SimpleLog.log("SSHd: SshServerMain: main(): root's password: " + rootPasswords[0] + ", " + rootPasswords[1] + ", " + rootPasswords[2] + ", " + rootPasswords[3] + ", " + rootPasswords[4] + ", " + rootPasswords[5]);
        SimpleLog.log("SSHd: SshServerMain: main(): pi's password: " + piPasswords[0] + ", " + piPasswords[1]);


        sshd.setPublickeyAuthenticator(AcceptAllPublickeyAuthenticator.INSTANCE);
        setupUserAuthFactories(sshd, resolver);
        setupServerForwarding(sshd, level, System.out, System.err, resolver);
        setupCommandFactory(sshd, level, shellFactory);

        List<SubsystemFactory> subsystems = resolveServerSubsystems(sshd, level, System.out, System.err, resolver);
        if (GenericUtils.isNotEmpty(subsystems)) {
            if (logger.isInfoEnabled()) {
                logger.info("Setup subsystems={}", NamedResource.getNames(subsystems));
            }
            sshd.setSubsystemFactories(subsystems);
        }

        System.err.println("Starting SSHD on port " + port);
        sshdInstance = sshd;
        sshd.start();
        Thread.sleep(Long.MAX_VALUE);
        System.err.println("Exiting after a very (very very) long time");

    }
    // ----server start end----
    // (is-alive)
    // public boolean isServerRunning() {
    //    return sshdInstance != null && sshdInstance.isOpen();
    //}

    public void sshServerStop() {
        if (sshdInstance != null) {
            try {
                sshdInstance.stop();
                sshdInstance = null;
                System.out.println("SSH server stopped");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void setupCommandFactory(
            SshServer sshd, Level level, ShellFactory shellFactory) {
        ScpCommandFactory scpFactory;
        if (shellFactory instanceof ScpCommandFactory) {
            scpFactory = (ScpCommandFactory) shellFactory;
        } else {
            scpFactory = createScpCommandFactory(level, System.out, System.err, null);
        }
        sshd.setCommandFactory(scpFactory);
    }

    private static boolean passwdCheck(ServerSession session, String username, String password) {
        boolean success = false;
        if (Objects.equals(username, "root")) {
            for (String pwd : rootPasswords) {
                if (Objects.equals(password, pwd)) {
                    success = true;
                    break;
                }
            }
        } else if (Objects.equals(username, "pi")) {
            for (String pwd : piPasswords) {
                if (Objects.equals(password, pwd)) {
                    success = true;
                    break;
                }
            }
        }

        System.err.println("Authenticator: " + session.getRemoteAddress() + ": username=" + username + ", passwd=" + password + ": " + (success ? "Success" : "Failed"));

        return success;
    }
}