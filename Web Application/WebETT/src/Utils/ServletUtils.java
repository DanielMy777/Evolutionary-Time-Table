package Utils;

import Chat.ChatList;
import Competative.ProblemList;
import Users.UserList;

import javax.servlet.ServletContext;

public class ServletUtils {
    private static final String USER_LIST_ATTRIBUTE_NAME = "userManager";
    private static final String CHAT_LIST_ATTRIBUTE_NAME = "chatManager";
    private static final String PROBLEM_LIST_ATTRIBUTE_NAME = "problemManager";

    /*
    Note how the synchronization is done only on the question and\or creation of the relevant managers and once they exists -
    the actual fetch of them is remained un-synchronized for performance POV
     */
    private static final Object userManagerLock = new Object();
    private static final Object chatManagerLock = new Object();
    private static final Object problemManagerLock = new Object();

    public static UserList getUserList(ServletContext servletContext) {

        synchronized (userManagerLock) {
            if (servletContext.getAttribute(USER_LIST_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(USER_LIST_ATTRIBUTE_NAME, new UserList());
            }
        }
        return (UserList) servletContext.getAttribute(USER_LIST_ATTRIBUTE_NAME);
    }

    public static ChatList getChatList(ServletContext servletContext) {
        synchronized (chatManagerLock) {
            if (servletContext.getAttribute(CHAT_LIST_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(CHAT_LIST_ATTRIBUTE_NAME, new ChatList());
            }
        }
        return (ChatList) servletContext.getAttribute(CHAT_LIST_ATTRIBUTE_NAME);
    }

    public static ProblemList getProblemList(ServletContext servletContext) {
        synchronized (problemManagerLock) {
            if (servletContext.getAttribute(PROBLEM_LIST_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(PROBLEM_LIST_ATTRIBUTE_NAME, new ProblemList());
            }
        }
        return (ProblemList) servletContext.getAttribute(PROBLEM_LIST_ATTRIBUTE_NAME);
    }
}
