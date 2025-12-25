import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;
import static com.raylib.Helpers.*;
import static com.raylib.Raylib.Color;
import java.util.ArrayList;
import java.util.Random;

import java.nio.file.*;

public class test {
    static long globalSeed = System.currentTimeMillis(); // Or use a fixed seed like 12345
    
    public static void main(String args[]) throws java.io.IOException {
        InitWindow(620, 620, "Demo");
        SetTargetFPS(120);
        
        Camera2D camera = newCamera2D(newVector2(300.0f,300.0f),newVector2(300.0f,300.0f),0,0.33f);
        
        int num_Rocks = 10;
        int num_Paper  = 10;
        int num_Scissors = 10;
        int scale = 40;
        String Path = ".\\img\\";
        float speed = 0.25f;
        int group = 600;
        
        Vector2 Rock_cen = newVector2(100,100);
        Vector2 Paper_cen = newVector2(500,100);
        Vector2 Scissors_cen = newVector2(300,500);
        
        // Initialize game with seed
        ArrayList<Obj> Rocks = add("R",Rock_cen,num_Rocks,group,speed,GRAY,scale,Path,globalSeed);
        ArrayList<Obj> Papers = add("P",Paper_cen,num_Paper,group,speed,BLUE,scale,Path,globalSeed + 1);
        ArrayList<Obj> Scissorss = add("S",Scissors_cen,num_Scissors,group,speed,GREEN,scale,Path,globalSeed + 2);
        
        // Reset button rectangle
        Rectangle resetButton = newRectangle(10, 90, 100, 30);
        // New seed button rectangle
        Rectangle newSeedButton = newRectangle(120, 90, 100, 30);
        
        float dt = GetFrameTime(); 
        while (!WindowShouldClose()) {
            dt = GetFrameTime();
            
            // Check if reset button is clicked
            if(IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
                Vector2 mousePos = GetMousePosition();
                
                // Reset with same seed
                if(CheckCollisionPointRec(mousePos, resetButton)) {
                    unloadTextures(Rocks);
                    unloadTextures(Papers);
                    unloadTextures(Scissorss);
                    
                    // Reset to same positions
                    Rocks = add("R",Rock_cen,num_Rocks,group,speed,GRAY,scale,Path,globalSeed);
                    Papers = add("P",Paper_cen,num_Paper,group,speed,BLUE,scale,Path,globalSeed + 1);
                    Scissorss = add("S",Scissors_cen,num_Scissors,group,speed,GREEN,scale,Path,globalSeed + 2);
                }
                
                // New random seed
                if(CheckCollisionPointRec(mousePos, newSeedButton)) {
                    unloadTextures(Rocks);
                    unloadTextures(Papers);
                    unloadTextures(Scissorss);
                    
                    // Generate new seed and reset
                    globalSeed = System.currentTimeMillis();
                    Rocks = add("R",Rock_cen,num_Rocks,group,speed,GRAY,scale,Path,globalSeed);
                    Papers = add("P",Paper_cen,num_Paper,group,speed,BLUE,scale,Path,globalSeed + 1);
                    Scissorss = add("S",Scissors_cen,num_Scissors,group,speed,GREEN,scale,Path,globalSeed + 2);
                    
                    System.out.println("New seed: " + globalSeed);
                }
            }
            
            // --- Update ---
            updating(dt,Rocks,Scissorss);
            updating(dt,Scissorss,Papers);
            updating(dt,Papers,Rocks);
            
            // --- Draw ---
            BeginDrawing();
            ClearBackground(BLACK);
            BeginMode2D(camera);
            
            Drawing(Rocks);
            Drawing(Scissorss);
            Drawing(Papers);
            
            EndMode2D();
            
            // Draw UI
            DrawText("Rocks: " + countAlive(Rocks), 10, 10, 20, GRAY);
            DrawText("Papers: " + countAlive(Papers), 10, 35, 20, BLUE);
            DrawText("Scissors: " + countAlive(Scissorss), 10, 60, 20, GREEN);
            DrawText("Seed: " + globalSeed, 230, 95, 15, LIGHTGRAY);
            
            // Draw reset button
            Color resetColor = CheckCollisionPointRec(GetMousePosition(), resetButton) ? DARKGRAY : GRAY;
            DrawRectangleRec(resetButton, resetColor);
            DrawRectangleLinesEx(resetButton, 2, WHITE);
            DrawText("RESET", 20, 95, 20, WHITE);
            
            // Draw new seed button
            Color newSeedColor = CheckCollisionPointRec(GetMousePosition(), newSeedButton) ? DARKGREEN : GREEN;
            DrawRectangleRec(newSeedButton, newSeedColor);
            DrawRectangleLinesEx(newSeedButton, 2, WHITE);
            DrawText("NEW", 135, 95, 20, WHITE);
            
            EndDrawing();
        }

        CloseWindow();
    }
    
    static void unloadTextures(ArrayList<Obj> objs) {
        for(Obj obj : objs) {
            UnloadTexture(obj.txt);
        }
    }
    
    static int countAlive(ArrayList<Obj> objs)
    {
        int count = 0;
        for(Obj obj : objs)
        {
            if(obj.alive)
                count++;
        }
        return count;
    }
    
    static ArrayList<Obj> add(String type,Vector2 cen,int num,int group,float speed,Color col,int scale,String Path,long seed)
    {
        ArrayList<Obj> Objs = new ArrayList<>();
        Random Rand = new Random(seed); // Use the provided seed
        for(int i = 0 ; i < num ; i++)
        {
            float x = cen.x() + (Rand.nextFloat() - 0.5f)*2*group;
            float y = cen.y() + (Rand.nextFloat() - 0.5f)*2*group;
            Objs.add(new Obj(type,newVector2(x,y),newVector2(0,0),speed,col,scale,Path));
        }
        return Objs;
    }
    
    static void updating (float dt,ArrayList<Obj> objs,ArrayList<Obj> tragets)
    {
        for(Obj obj : objs)
        {
            if(obj.alive)
            {
                Obj near = obj.Findnear(tragets) ;
                if(near!= null)
                    obj.Update(dt,near);
            }
        }
    }
    
    static void Drawing(ArrayList<Obj> objs)
    {
        for(Obj obj : objs)
        {
            if(obj.alive)
            {
                obj.Draw();
            }
        }
    }
}