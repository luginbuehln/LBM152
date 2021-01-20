package ch.bzz.refproject.data;

import ch.bzz.refproject.model.Category;
import ch.bzz.refproject.model.Project;
import ch.bzz.refproject.util.Result;

import javax.print.DocFlavor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ProjectDao implements Dao<Project, String>{
    /**
     * default constructor
     */
    public ProjectDao() {}

    /**
     * list all project in the table "RefProject"
     *
     * @return list of projects
     */

    @Override
    public List<Project> getAll() {
        List<Project> projectList = new ArrayList<>();
        String sqlQuery="SELECT p.projectUUID, p.title, p.status, p.startDate, " +
                " p.endDate, p.categoryUUID, c.title " +
                " FROM Project AS p JOIN Category AS c USING (categoryUUID) " +
                " ORDER BY title, startDate";

        try {
            ResultSet resultSet = MySqlDB.sqlSelect(sqlQuery);
            while (resultSet.next()){
                Project project = new Project();
                setValues(resultSet, project);
                projectList.add(project);
            }
        } catch (SQLException sqlEx){
            MySqlDB.printSQLException(sqlEx);
            throw new RuntimeException();
        } finally {

            MySqlDB.sqlClose();
        }
        return projectList;
    }


    /**
     * read a single project from the table RefProject
     *
     * @param projectUUID primary kex
     * @return project object
     */

    @Override
    public Project getEntity(String projectUUID) {
        Project project = new Project();

        String sqlQuery = "SELECT p.projectUUID, p.title, p.status, p.startDate, " +
                " p.endDate, p.categoryUUID, c.title " +
                " FROM Project AS p JOIN Category AS c USING (categoryUUID) " +
                " WHERE projectUUID=?";

        Map<Integer, String> values = new HashMap<>();
        values.put(1,projectUUID);
        try {
            ResultSet resultSet = MySqlDB.sqlSelect(sqlQuery);
            if (resultSet.next()){
                setValues(resultSet, project);
            }
        } catch (SQLException sqlEx){
            sqlEx.printStackTrace();
            throw new RuntimeException();
        } finally {
            MySqlDB.sqlClose();
        }
        return project;
    }

    /**
     * deletes a project in the table RefProject
     *
     * @param projectUUID primary kex
     * @return Result
     */

    @Override
    public Result delete(String projectUUID) {
        String sqlQuery= "DELETE FROM Project" +
                " WHERE projectUUID=?"
        Map<Integer, String> values= new HashMap<>();
        values.put(1, projectUUID);
        try {
            return MySqlDB.sqlUpdate(sqlQuery, values);

        } catch (SQLException sqlEx){
            throw new RuntimeException();
        }
    }

    /**
     * saves a new project in the table RefProject
     *
     * @param project
     * @return Result
     */

    @Override
    public Result save(Project project) {
        Map<Integer, String> values = new HashMap<>();
        String sqlQuery;
        if (project.getProjectUUID() == null){
            project.setProjectUUID(UUID.randomUUID().toString());
            sqlQuery="INSERT INTO Project";
        } else {
            sqlQuery="REPLACE Project";
        }
        sqlQuery += "SET projectUUID=?, "+
                " categoryUUID=?, " +
                " title=?, " +
                " status=?, " +
                " startDate=?, " +
                " endDate=?";

        values.put(1, project.getProjectUUID());
        values.put(2, project.getCategory().getCategoryUUID());
        values.put(3, project.getTitle());
        values.put(4, project.getStatus());
        values.put(5, String.valueOf(project.getStartDate()));
        values.put(5, String.valueOf(project.getEndDate()));

        try {
            return MySqlDB.sqlUpdate(sqlQuery, values);
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * Update a project in the table RefProject
     *
     * @param project
     * @return Results
     */

    public Result update(Project project){
        return null;
    }

    private void setValues(ResultSet resultSet, Project project) throws SQLException{
        project.setProjectUUID(resultSet.getString("projectUUID"));
        project.setCategory(new Category());
        project.getCategory().setCategoryUUID(resultSet.getString("categoryUUID"));
        project.getCategory().setTitle(resultSet.getString("title"));
        project.setTitle(resultSet.getString("title"));
        project.setStatus(resultSet.getString("status"));
    }
}
