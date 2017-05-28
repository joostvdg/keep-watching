/*jshint esversion: 6 */

import React from 'react';
import Button from 'react-bootstrap/lib/Button';
import Modal from 'react-bootstrap/lib/Modal';
import Glyphicon from 'react-bootstrap/lib/Glyphicon'

const rest = require('rest');
const mime = require('rest/interceptor/mime');

/*
 "id": 1,
 "name": "Logan",
 "studio": "",
 "director": "James Mangold",
 "notableActors": "Hugh Jackman,Patrick Stewart",
 "releaseYear": "2017",
 "releaseDate": "2017-03-02",
 "genre": "Action,Drama,Sci-Fi",
 "imdbLink": "http://www.imdb.com/title/tt3315342/",
 "seen": false,
 "cinemaWorthy": true,
 "wanted": true
 */

export function ShowMovie(props) {
    const movie = props.movie;
    return (
        <table>
            <tbody>
                <tr>
                    <th>Name</th>
                    <td>{movie.name}</td>
                </tr>
                <tr>
                    <th>Description</th>
                    <td>{movie.studio}</td>
                </tr>
                <tr>
                    <th>Notable Actors</th>
                    <td>{movie.notableActors}</td>
                </tr>
                <tr>
                    <th>Year of release</th>
                    <td>{movie.releaseYear}</td>
                </tr>
                <tr>
                    <th>Release date</th>
                    <td>{movie.releaseDate}</td>
                </tr>
                <tr>
                    <th>Genre</th>
                    <td>{movie.genre}</td>
                </tr>
                <tr>
                    <th>IMDB</th>
                    <td>{movie.imdbLink}</td>
                </tr>
                <tr>
                    <th>Seen yet?</th>
                    <td><Glyphicon glyph={movie.seen ? 'eye-open' : 'eye-close'}/></td>
                </tr>
                <tr>
                    <th>Worthy of seeing in Cinema?</th>
                    <td><Glyphicon glyph={movie.cinemaWorthy ? 'eye-open' : 'eye-close'}/></td>
                </tr>
                <tr>
                    <th>Do we still want to watch it?</th>
                    <td><Glyphicon glyph={movie.wanted ? 'eye-open' : 'eye-close'}/></td>
                </tr>

            </tbody>
        </table>
    );
}

class DeleteButton extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            movie: props.movie
        };
        this.deleteMovie = this.deleteMovie.bind(this);
    }

    deleteMovie() {
        const id = this.state.movie.id;
        console.log("Going to delete movie: " + id);
        let client = rest.wrap(mime);
        client({ path: '/movies/'+id, method: 'DELETE' });
    }

    render() {
        return (
            <Button bsStyle="danger" bsSize="small" onClick={this.deleteMovie}><Glyphicon glyph="trash"/></Button>
        );
    }
}

export class Movie extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            movie: props.movie
        };
    }

    render() {
        const movie = this.state.movie;
        return (
            <tr>
                <th>{movie.name}</th>
                <th>{movie.releaseYear}</th>
                <th>{movie.genre}</th>
                <th><Glyphicon glyph={movie.seen ? 'eye-open' : 'eye-close'}/></th>
                <th><Glyphicon glyph={movie.cinemaWorthy ? 'eye-open' : 'eye-close'}/></th>
                <th><Glyphicon glyph={movie.wanted ? 'eye-open' : 'eye-close'}/></th>
                <th><Button bsStyle="info" bsSize="small" href={movie.imdbLink} target="_blanc"><Glyphicon
                    glyph="film"/></Button></th>
                <td><ShowMovieModal movie={movie}/></td>
                <td><DeleteButton movie={movie} /></td>
            </tr>
        );
    }
}

export class ShowMovieModal extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            showModal: false
        };
        this.close = this.close.bind(this);
        this.open = this.open.bind(this);
    }

    close() {
        this.setState({ showModal: false });
    }

    open() {
        this.setState({ showModal: true });
    }

    render() {
        return (
            <div>
                <Button bsStyle="info" bsSize="small" onClick={this.open}>
                    <Glyphicon glyph="zoom-in" />
                </Button>

                <Modal show={this.state.showModal} onHide={this.close}>
                    <Modal.Header closeButton>
                        <Modal.Title>Movie</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <ShowMovie movie={this.props.movie} />
                    </Modal.Body>
                    <Modal.Footer>
                        <Button onClick={this.close}>Close</Button>
                    </Modal.Footer>
                </Modal>
            </div>
        );
    }
}