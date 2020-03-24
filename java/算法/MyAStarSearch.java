package com.example;


import java.util.ArrayList;
import java.util.List;

public class MyAStarSearch {

    /*----------------------------A星寻路算法----------------------*/


    // 迷宫地图
    private static final int[][] MAZE = {
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
    };

    /**
     * Grid 表示当前位置属性
     * <p>
     *     x : x 坐标
     *     y : y 坐标
     *     g : 从起点走到当前格子的成本，也就是已经花费了多少步
     *     h : 在不考虑障碍的情况下，从当前格子走到目标格子的距离，也就是离目标还有多远
     *     f : G和H的综合评估，也就是从起点到达当前格子，再从当前格子到达目标格子的总步数
     *
     *     f = g + h
     * </p>
     */
    private static class Grid{
        private int x;
        private int y;
        private int f;
        private int g;
        private int h;
        private Grid parent;

        private Grid(int x, int y){
            this.x = x;
            this.y = y;
        }

        private void initGrid(Grid parent, Grid end){
            this.parent = parent;
            if (parent != null){
                this.g = parent.g + 1;
            }else {
                this.g = 1;
            }
            this.h = Math.abs(this.x - end.x) + Math.abs(this.y - end.y);
            this.f = this.g + this.h;
        }
    }


    /**
     *  核心算法
     * @param start 起始位置
     * @param end 终止位置
     * @return {@code Grid}
     */
    private static Grid aStarSearch(Grid start, Grid end){

        // 可到达的格子
        ArrayList<Grid> openList = new ArrayList<>();

        // 已到达的格子
        ArrayList<Grid> closeList = new ArrayList<>();

        // 把起点加入 openList
        openList.add(start);
        // 主循环，每一轮检查1个当前方格节点
        while (openList.size() > 0){
            // 在openList中查找 F值最小的节点，将其作为当前方格节点
            Grid currentGrid = findMinGrid(openList);
            // 将当前方格节点从openList中移除
            openList.remove(currentGrid);
            // 当前方格节点进入 closeList
            closeList.add(currentGrid);
            // 找到所有邻近节点
            List<Grid> neighbors = findNeighbors(currentGrid, openList, closeList);
            for (Grid grid : neighbors){
                if (!openList.contains(grid)){
                    // 邻近节点不在openList 中，标记“父节点”、G、H、F，并放入openList
                    grid.initGrid(currentGrid, end);
                    openList.add(grid);
                }
            }
            // 如果终点在openList中，直接返回终点格子
            for (Grid grid : openList){
                if ((grid.x == end.x) && (grid.y == end.y)){
                    return grid;
                }
            }
        }
        // openList用尽，仍然找不到终点，说明终点不可到达，返回空
        return null;
    }

    private static Grid findMinGrid(ArrayList<Grid> openList){
        Grid tempGrid = openList.get(0);
        for (Grid grid : openList){
            if (grid.f < tempGrid.f){
                tempGrid = grid;
            }
        }
        return tempGrid;
    }

    /**
     * 找到当前 grid 四周可用数据
     * @param grid 当前 grid
     * @param openList openList
     * @param closeList closeList
     * @return {@code List<Grid>}
     */
    private static ArrayList<Grid> findNeighbors(Grid grid, List<Grid> openList, List<Grid> closeList){
        ArrayList<Grid> gridList = new ArrayList<>();
        if (isValidGrid(grid.x, grid.y - 1, openList, closeList)){
            gridList.add(new Grid(grid.x, grid.y - 1));
        }
        if (isValidGrid(grid.x, grid.y + 1 , openList, closeList)){
            gridList.add(new Grid(grid.x, grid.y + 1));
        }
        if (isValidGrid(grid.x - 1, grid.y, openList, closeList)){
            gridList.add(new Grid(grid.x - 1, grid.y));
        }
        if (isValidGrid(grid.x + 1, grid.y, openList, closeList)){
            gridList.add(new Grid(grid.x + 1, grid.y));
        }
        return gridList;
    }

    /**
     * 检测当前坐标是否可用
     * @param x x 坐标
     * @param y y 坐标
     * @param openList openList
     * @param closeList closeList
     * @return boolean 是否可用
     */
    private static boolean isValidGrid(int x, int y, List<Grid> openList, List<Grid> closeList){
        // 是否超越边界
        if (x < 0 || x >= MAZE.length || y < 0 || y >= MAZE[0].length){
            return false;
        }
        // 是否有障碍物
        if (MAZE[x][y] == 1){
            return false;
        }
        // 是否已经在 openList 中
        if (containGrid(openList, x, y)){
            return false;
        }
        // 是否已经自 closeList 中
        if (containGrid(closeList, x, y)){
            return false;
        }
        return true;
    }

    /**
     * 检测数组是否包含某个坐标
     * @param grids 数组
     * @param x x 坐标
     * @param y y 坐标
     * @return boolean
     */
    private static boolean containGrid(List<Grid> grids, int x, int y){
        for (Grid g : grids){
            if ((g.x == x) && (g.y == y)){
                return true;
            }
        }
        return false;
    }


    public static void main(String[] args) {
        // 设置起点和终点
        Grid startGrid = new Grid(2, 1);
        Grid endGrid = new Grid(2, 5);
        // 搜索迷宫终点
        Grid resultGrid = aStarSearch(startGrid, endGrid);

        // 回溯迷宫路径
        ArrayList<Grid> path = new ArrayList<>();
        while (resultGrid != null){
            path.add(new Grid(resultGrid.x, resultGrid.y));
            resultGrid = resultGrid.parent;
        }
        // 输出迷宫和路径，路径用*表示
        for (int i = 0; i < MAZE.length; i++) {
            for (int j = 0; j < MAZE[0].length; j++) {
                if (containGrid(path, i, j)){
                    System.out.print("*, ");
                }else {
                    System.out.print(MAZE[i][j] + ", ");
                }
            }
            System.out.println();
        }
    }
}
