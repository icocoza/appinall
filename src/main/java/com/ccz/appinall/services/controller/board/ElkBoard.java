package com.ccz.appinall.services.controller.board;

import com.ccz.appinall.services.controller.board.RecDataBoard.AddBoard;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;

@JsonInclude(Include.NON_NULL)
@Getter
public class ElkBoard {
    private String title;
    private String content;
    private String writer;
    private String category;
    private String boardid;
    
    public ElkBoard(String boardid, String category, String writer, AddBoard board) {
    	this.boardid = boardid;
    	this.title = board.title;
    	this.content = board.content;
    	this.category = category;
    	this.writer = writer;
    }
    
    public ElkBoard(String category, String writer, String title, String content) {
    	this.category = category;
    	this.writer = writer;
    	this.title = title;
    	this.content = content;
    }
}
