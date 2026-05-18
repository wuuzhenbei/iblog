<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${param.title} - iBlog 管理后台</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #f5f5f5; }
        .container { max-width: 1200px; margin: 0 auto; padding: 20px; }
        .card { background: #fff; border-radius: 8px; padding: 20px; margin-bottom: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
        .btn { display: inline-block; padding: 8px 16px; border-radius: 4px; text-decoration: none; font-size: 14px; cursor: pointer; border: none; }
        .btn-primary { background: #1890ff; color: #fff; }
        .btn-danger { background: #ff4d4f; color: #fff; }
        .btn-success { background: #52c41a; color: #fff; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 12px; text-align: left; border-bottom: 1px solid #f0f0f0; }
        th { background: #fafafa; font-weight: 600; }
        .tag { display: inline-block; padding: 2px 8px; border-radius: 4px; font-size: 12px; }
        .tag-green { background: #f6ffed; color: #52c41a; border: 1px solid #b7eb8f; }
        .tag-gray { background: #f5f5f5; color: #999; border: 1px solid #d9d9d9; }
        .tag-red { background: #fff2f0; color: #ff4d4f; border: 1px solid #ffccc7; }
        .tag-blue { background: #e6f7ff; color: #1890ff; border: 1px solid #91d5ff; }
        .pagination { display: flex; gap: 8px; margin-top: 20px; justify-content: center; }
        .pagination a, .pagination span { padding: 6px 12px; border: 1px solid #d9d9d9; border-radius: 4px; text-decoration: none; color: #333; }
        .pagination .current { background: #1890ff; color: #fff; border-color: #1890ff; }
        .stat-cards { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin-bottom: 20px; }
        .stat-card { background: #fff; border-radius: 8px; padding: 20px; text-align: center; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
        .stat-card .number { font-size: 32px; font-weight: bold; color: #1890ff; }
        .stat-card .label { color: #999; margin-top: 8px; }
    </style>
</head>
<body>
