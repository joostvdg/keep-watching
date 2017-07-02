/*jshint esversion: 6 */

import React from 'react';
import Cookies from 'universal-cookie';

import Button from 'react-bootstrap/lib/Button';
import Modal from 'react-bootstrap/lib/Modal';
import Glyphicon from 'react-bootstrap/lib/Glyphicon'
import Form from 'react-bootstrap/lib/Form';
import FormControl from 'react-bootstrap/lib/FormControl'

import FormGroup from 'react-bootstrap/lib/FormGroup';
import ControlLabel from 'react-bootstrap/lib/ControlLabel';

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
                <td><ShowMovieEditModal movie={movie} edit="true"/></td>
                <td><DeleteButton movie={movie} /></td>
            </tr>
        );
    }
}

export class ShowMovieEditModal extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            showModal: false,
            movie:  props.movie,
            edit: props.edit
        };
        this.close = this.close.bind(this);
        this.open = this.open.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    close() {
        this.setState({ showModal: false });
    }

    open() {
        this.setState({ showModal: true });
    }

    handleChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const key = target.name;

        const updatedMovie = this.state.movie;
        updatedMovie[key] = value;
        this.setState({
            movie: updatedMovie
        });
    }

    handleSubmit(event) {
        event.preventDefault();

        const cookies = new Cookies();
        const xsrfToken = cookies.get('XSRF-TOKEN');
        console.log(xsrfToken);

        console.log("handling submit");
        console.log(this.state.movie);

        fetch('/movies', {
            method: this.state.edit ? 'PUT' : 'POST',
            headers: {
                'Accept': 'application/json, application/xml, text/plain, text/html, */*',
                'Content-Type': 'application/json',
                'X-XSRF-TOKEN': xsrfToken
            },
            credentials: 'same-origin',
            mode: 'cors',
            redirect: 'follow',
            body: JSON.stringify({
                id: this.state.movie.id,
                name: this.state.movie.name,
                studio: this.state.movie.studio,
                director: this.state.movie.director,
                notableActors: this.state.movie.notableActors,
                releaseYear: this.state.movie.releaseYear,
                releaseDate: this.state.movie.releaseDate,
                genre: this.state.movie.genre,
                imdbLink: this.state.movie.imdbLink,
                seen: this.state.movie.seen,
                cinemaWorthy: this.state.movie.cinemaWorthy,
                wanted: this.state.movie.wanted,
            })
        });

        this.state = {
            showModal: false,
            movie : {
                    id: '',
                    name: '',
                    studio: '',
                    director: '',
                    notableActors: '',
                    releaseYear: '',
                    releaseDate: '',
                    genre: '',
                    imdbLink: '',
                    seen: '',
                    cinemaWorthy: '',
                    wanted: ''
            }
        }
    }

    render() {
        return (
            <div>
                <Button bsStyle="success" bsSize={this.state.edit ? 'small' : 'large'} onClick={this.open}>
                    <Glyphicon glyph={this.state.edit ? 'edit' : 'plus'}/>
                </Button>

                <Modal show={this.state.showModal} onHide={this.close}>
                    <Modal.Header closeButton>
                        <Modal.Title>New Movie</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <Form onSubmit={this.handleSubmit}>
                            <FormGroup controlId="newMovieInputName">
                                <ControlLabel>Name</ControlLabel>
                                <FormControl type="text" value={this.state.movie.name} name="name" placeholder="name" onChange={this.handleChange} />
                                <FormControl.Feedback />
                            </FormGroup>
                            <FormGroup controlId="newMovieInputStudio">
                                <ControlLabel>Studio</ControlLabel>
                                <FormControl type="text" value={this.state.movie.studio} name="studio" placeholder="studio" onChange={this.handleChange} />
                                <FormControl.Feedback />
                            </FormGroup>
                            <FormGroup controlId="newMovieInputDirector">
                                <ControlLabel>Director</ControlLabel>
                                <FormControl type="text" value={this.state.movie.director} name="director" placeholder="director" onChange={this.handleChange} />
                                <FormControl.Feedback />
                            </FormGroup>
                            <FormGroup controlId="newMovieInputNotableActors">
                                <ControlLabel>Notable Actions (comma separated list)</ControlLabel>
                                <FormControl type="text" value={this.state.movie.notableActors} name="notableActors" placeholder="notableActors" onChange={this.handleChange} />
                                <FormControl.Feedback />
                            </FormGroup>
                            <FormGroup controlId="newMovieInputYear">
                                <ControlLabel>Year of release (e.g. 2017)</ControlLabel>
                                <FormControl type="text" value={this.state.movie.releaseYear} name="releaseYear" placeholder="releaseYear" onChange={this.handleChange} />
                                <FormControl.Feedback />
                            </FormGroup>
                            <FormGroup controlId="newMovieInputGenre">
                                <ControlLabel>Genre's (comma separated list)</ControlLabel>
                                <FormControl type="text" value={this.state.movie.genre} name="genre" placeholder="genre" onChange={this.handleChange} />
                                <FormControl.Feedback />
                            </FormGroup>
                            <FormGroup controlId="newMovieInputImdb">
                                <ControlLabel>IMDB Link</ControlLabel>
                                <FormControl type="text" value={this.state.movie.imdbLink} name="imdbLink" placeholder="imdbLink" onChange={this.handleChange} />
                                <FormControl.Feedback />
                            </FormGroup>
                            <FormGroup controlId="">
                                <ControlLabel>Seen</ControlLabel>
                                <FormControl type="checkbox" checked={this.state.movie.seen} value={this.state.movie.seen} name="seen" onChange={this.handleChange} />
                                <FormControl.Feedback />
                            </FormGroup>
                            <FormGroup controlId="newMovieInputCinema">
                                <ControlLabel>Cinema</ControlLabel>
                                <FormControl type="checkbox" checked={this.state.movie.cinemaWorthy} value={this.state.movie.cinemaWorthy} name="cinemaWorthy" onChange={this.handleChange} />
                                <FormControl.Feedback />
                            </FormGroup>
                            <FormGroup controlId="newMovieInputWanted">
                                <ControlLabel>Want to see</ControlLabel>
                                <FormControl type="checkbox" checked={this.state.movie.wanted} value={this.state.movie.wanted} name="wanted" onChange={this.handleChange} />
                                <FormControl.Feedback />
                            </FormGroup>

                            <Button type="submit">{this.state.edit ? 'update' : 'create'}</Button>
                        </Form>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button onClick={this.close}>Close</Button>
                    </Modal.Footer>
                </Modal>
            </div>
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