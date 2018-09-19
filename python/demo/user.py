# -*- coding:utf-8 -*-

from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, Integer, String, and_, or_, func
from sqlalchemy.orm import sessionmaker


# 创建对象基类
Base = declarative_base()


class User(Base):
    """
    1.指定表名
    2.指定表结构
    """
    __tablename__ = 'user'

    def __init__(self, name=None, age=None, address=None):
        self.user_name = name
        self.user_age = age
        self.user_address = address

    id = Column(Integer, primary_key=True, autoincrement=True)
    user_name = Column('userName', String(255))
    user_age = Column('userAge', Integer)
    user_address = Column('userAddress', String(255))

    def __str__(self):
        return self.user_name

    def __repr__(self):
        return self.user_age


# 数据库连接 echo=True 打印sql
engine = create_engine("mysql+pymysql://root:123456@localhost:3306/java?charset=utf8", echo=True)

# 创建表结构
# Base.metadata.create_all(engine)
# 删除表
# Base.metadata.drop_all(engine)


# session
Session = sessionmaker(bind=engine)
session = Session()

if __name__ == '__main__':

    # 增
    u = User('user', 10, 'address')
    u1 = User()
    u1.user_name = 'user1'
    u1.user_age = 11
    u1.user_address = 'address'
    session.add(u)
    session.add_all([u1])
    session.commit()

    # 删
    session.query(User).filter(User.id > 10).delete()
    session.query(User).filter_by(user_name='user').delete()
    session.commit()

    # 改
    session.query(User).filter(User.id == 1).update({User.user_name: 'user_name'})
    session.query(User).filter_by(user_name='user_name').update({'user_name': 'test_name'})
    session.commit()

    # 查
    user = session.query(User).first()

    # and
    users = session.query(User).filter(User.id.in_([1, 2, 3])
                                       , User.user_name == 'test_name').all()

    users1 = session.query(User).filter(and_(User.id == 1, User.user_name == 'test_name')).all()

    # or
    users2 = session.query(User).filter(or_(User.id > 1, User.user_name == 'test_name')).all()

    # like
    users3 = session.query(User).filter(User.user_name.like('name%')).all()

    # limit
    users4 = session.query(User)[0:1]

    # sort
    users5 = session.query(User).order_by(User.id.desc()).all()

    # group
    users6 = session.query(User).group_by(User.id).all()

    # func
    max_id = session.query(func.max(User.id)).one()
    sum_age = session.query(func.sum(User.user_age)).one()
