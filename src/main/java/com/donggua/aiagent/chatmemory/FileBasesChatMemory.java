package com.donggua.aiagent.chatmemory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: ajie
 * Date: 2026-06-04 10:54
 * Description: 文件持久化自定义chatMemory
 */
public class FileBasesChatMemory implements ChatMemory {

    // 创建一个文件存储路径
    private final String BASE_DIR;

    // 创建一个序列化的工具
    private static final Kryo kryo = new Kryo();


    static {
        // 初始化的时候不需要手动注册
        kryo.setRegistrationRequired(false);
        // 设置标准实例化策略
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }

    /**
     * 设置构造函数 讲用户传入的地址设置到这里
     *
     * @param dir
     */
    public FileBasesChatMemory(String dir) {
        this.BASE_DIR = dir;
        //判断文件是否存在
        File baseDir = new File(dir);
        // 如果文件不存在
        if (!baseDir.exists()) {
            // 创建文件 防止报错
            baseDir.mkdirs();
        }
    }

    /**
     * 新增一条消息列表
     *
     * @param conversationId
     * @param message
     */
    @Override
    public void add(String conversationId, Message message) {
        // 查询当前的消息列表
        List<Message> messageList = getOrCreateConversation(conversationId);
        messageList.add(message);
        saveConversation(conversationId, messageList);
    }

    /**
     * 新增多条消息列表
     *
     * @param conversationId
     * @param messages
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        // 查询当前的消息列表
        List<Message> messageList = getOrCreateConversation(conversationId);
        messageList.addAll(messages);
        saveConversation(conversationId, messageList);
    }

    @Override
    public List<Message> get(String conversationId) {
        return getOrCreateConversation(conversationId);
    }

    @Override
    public void clear(String conversationId) {
        File file = getConversationIdFiles(conversationId);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 获取或创建会话消息列表
     *
     * @param conversationId 会话id
     * @return
     */
    private List<Message> getOrCreateConversation(String conversationId) {
        // 根据会话id查询文件是否存在
        File file = getConversationIdFiles(conversationId);
        // 创建一个存储会话消息的列表
        List<Message> messages = new ArrayList<>();
        // 判断文件是否存在
        if (file.exists()) {
            // 如果存在 读取文件信息
            try (Input input = new Input(new FileInputStream(file))) {
                // 通过kryo里面的方法解析文件中的数据 然后转换成messages返回 传入读取的input和需要转换成什么类型的数据的class
                messages = kryo.readObject(input, ArrayList.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return messages;
    }

    /**
     * 根据会话id保存会话消息列表到文件中
     *
     * @param conversationId 会话id
     * @param messages       会话消息列表
     */
    private void saveConversation(String conversationId, List<Message> messages) {
        // 根据会话id查询会话是否存在
        File file = getConversationIdFiles(conversationId);
        try (Output output = new Output(new FileOutputStream(file))) {
            // 通过kryo里面的方法将会话列表数据写入文件中
            kryo.writeObject(output, messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据会话id在文件路径中找到对应的文件
     *
     * @param conversationId 会话id
     * @return
     */
    private File getConversationIdFiles(String conversationId) {
        return new File(BASE_DIR, conversationId + ".kryo");
    }

}
